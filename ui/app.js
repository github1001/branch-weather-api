const API_BASE_URL = "http://localhost:8080/api";

const state = {
  branches: [],
  weatherByBranchId: new Map(),
  loadingWeatherIds: new Set(),
  searchTerm: ""
};

const branchGrid = document.getElementById("branchGrid");
const emptyState = document.getElementById("emptyState");
const searchInput = document.getElementById("searchInput");
const refreshAllButton = document.getElementById("refreshAllButton");
const branchCount = document.getElementById("branchCount");
const weatherCount = document.getElementById("weatherCount");
const statusMessage = document.getElementById("statusMessage");

document.addEventListener("DOMContentLoaded", () => {
  searchInput.addEventListener("input", event => {
    state.searchTerm = event.target.value.trim().toLowerCase();
    renderBranches();
  });

  refreshAllButton.addEventListener("click", loadBranches);

  branchGrid.addEventListener("click", event => {
    const button = event.target.closest("[data-weather-id]");
    if (!button) {
      return;
    }

    const branchId = Number(button.dataset.weatherId);
    loadWeather(branchId);
  });

  loadBranches();
});

async function loadBranches() {
  setStatus("loading", "Loading branches from the backend...");
  refreshAllButton.disabled = true;

  try {
    // The backend returns a custom paginated response. A large size is requested
    // so this simple showcase can render all current seed records at once.
    const response = await fetch(`${API_BASE_URL}/branches?page=0&size=100`, {
      headers: { Accept: "application/json" }
    });

    if (!response.ok) {
      throw new Error(await getErrorMessage(response));
    }

    const payload = await response.json();
    state.branches = extractBranches(payload);
    state.weatherByBranchId.clear();
    state.loadingWeatherIds.clear();

    setStatus("success", `Loaded ${state.branches.length} branches.`);
    renderBranches();
  } catch (error) {
    console.error(error);
    state.branches = [];
    renderBranches();
    setStatus(
      "error",
      `Unable to load branches. ${error.message} Confirm that Spring Boot is running on port 8080 and CORS is enabled.`
    );
  } finally {
    refreshAllButton.disabled = false;
  }
}

async function loadWeather(branchId) {
  if (state.loadingWeatherIds.has(branchId)) {
    return;
  }

  state.loadingWeatherIds.add(branchId);
  renderBranches();

  try {
    const response = await fetch(`${API_BASE_URL}/branches/${branchId}/weather`, {
      headers: { Accept: "application/json" }
    });

    if (!response.ok) {
      throw new Error(await getErrorMessage(response));
    }

    const payload = await response.json();
    state.weatherByBranchId.set(branchId, normalizeWeather(payload));
    setStatus("success", "Weather updated successfully.");
  } catch (error) {
    console.error(error);
    state.weatherByBranchId.set(branchId, {
      error: error.message || "Weather is currently unavailable."
    });
    setStatus("error", `Unable to retrieve weather for branch ${branchId}.`);
  } finally {
    state.loadingWeatherIds.delete(branchId);
    renderBranches();
  }
}

function extractBranches(payload) {
  if (Array.isArray(payload)) {
    return payload;
  }

  // Supports common custom page response names and Spring Page's "content".
  const candidates = [
    payload.content,
    payload.items,
    payload.data,
    payload.branches,
    payload.results
  ];

  const branchArray = candidates.find(Array.isArray);
  return branchArray ?? [];
}

function normalizeWeather(payload) {
  const weather = payload.weather ?? payload.currentWeather ?? payload.current ?? payload;

  return {
    temperature: firstDefined(
      weather.temperature,
      weather.temperatureCelsius,
      weather.temperature_2m
    ),
    humidity: firstDefined(
      weather.humidity,
      weather.relativeHumidity,
      weather.relative_humidity_2m
    ),
    windSpeed: firstDefined(
      weather.windSpeed,
      weather.wind_speed,
      weather.wind_speed_10m
    ),
    weatherCode: firstDefined(
      weather.weatherCode,
      weather.weather_code
    ),
    observedAt: firstDefined(
      weather.observedAt,
      weather.time,
      payload.observedAt
    )
  };
}

function firstDefined(...values) {
  return values.find(value => value !== undefined && value !== null);
}

function renderBranches() {
  const filteredBranches = state.branches.filter(branch => {
    const searchableText = [
      branch.branchCode,
      branch.branchName,
      branch.city,
      branch.country,
      branch.address
    ]
      .filter(Boolean)
      .join(" ")
      .toLowerCase();

    return searchableText.includes(state.searchTerm);
  });

  branchCount.textContent = String(filteredBranches.length);
  weatherCount.textContent = String(state.weatherByBranchId.size);

  emptyState.classList.toggle("hidden", filteredBranches.length !== 0);
  branchGrid.innerHTML = filteredBranches.map(createBranchCard).join("");
}

function createBranchCard(branch) {
  const weather = state.weatherByBranchId.get(branch.id);
  const isLoading = state.loadingWeatherIds.has(branch.id);
  const active = branch.active !== false;

  return `
    <article class="group overflow-hidden rounded-3xl border border-white bg-white shadow-soft transition duration-300 hover:-translate-y-1">
      <div class="bg-gradient-to-br from-sky-500 via-cyan-500 to-indigo-600 p-5 text-white">
        <div class="flex items-start justify-between gap-4">
          <div>
            <p class="text-sm font-semibold text-white/80">${escapeHtml(branch.branchCode ?? "—")}</p>
            <h2 class="mt-1 text-xl font-bold">${escapeHtml(branch.branchName ?? "Unnamed branch")}</h2>
          </div>
          <span class="rounded-full bg-white/20 px-3 py-1 text-xs font-semibold backdrop-blur">
            ${active ? "Active" : "Inactive"}
          </span>
        </div>

        <p class="mt-4 text-sm text-white/85">
          ${escapeHtml([branch.city, branch.country].filter(Boolean).join(", ") || "Location unavailable")}
        </p>
      </div>

      <div class="p-5">
        ${createWeatherSection(weather, isLoading)}

        <div class="mt-5 border-t border-slate-100 pt-4">
          <p class="text-sm leading-6 text-slate-600">
            ${escapeHtml(branch.address ?? "Address unavailable")}
          </p>

          <div class="mt-3 flex flex-wrap gap-2 text-xs font-medium text-slate-500">
            <span class="rounded-full bg-slate-100 px-3 py-1.5">
              ${escapeHtml(branch.phoneNumber ?? "No phone")}
            </span>
            ${
              branch.latitude != null && branch.longitude != null
                ? `<a
                     href="https://www.google.com/maps?q=${encodeURIComponent(branch.latitude)},${encodeURIComponent(branch.longitude)}"
                     target="_blank"
                     rel="noreferrer"
                     class="rounded-full bg-sky-50 px-3 py-1.5 text-sky-700 transition hover:bg-sky-100"
                   >
                     View map ↗
                   </a>`
                : ""
            }
          </div>
        </div>

        <button
          type="button"
          data-weather-id="${branch.id}"
          ${isLoading ? "disabled" : ""}
          class="mt-5 inline-flex w-full items-center justify-center gap-2 rounded-xl bg-slate-900 px-4 py-3 font-semibold text-white transition hover:bg-slate-700 disabled:cursor-wait disabled:opacity-60"
        >
          ${isLoading ? "Loading weather..." : weather ? "Refresh weather" : "Load weather"}
        </button>
      </div>
    </article>
  `;
}

function createWeatherSection(weather, isLoading) {
  if (isLoading) {
    return `
      <div class="grid grid-cols-3 gap-3">
        ${createSkeletonMetric()}
        ${createSkeletonMetric()}
        ${createSkeletonMetric()}
      </div>
    `;
  }

  if (!weather) {
    return `
      <div class="rounded-2xl bg-slate-50 p-5 text-center">
        <div class="text-3xl" aria-hidden="true">⛅</div>
        <p class="mt-2 font-semibold">Weather not loaded</p>
        <p class="mt-1 text-sm text-slate-500">Select “Load weather” to retrieve live conditions.</p>
      </div>
    `;
  }

  if (weather.error) {
    return `
      <div class="rounded-2xl border border-rose-200 bg-rose-50 p-4 text-sm text-rose-700">
        ${escapeHtml(weather.error)}
      </div>
    `;
  }

  return `
    <div class="grid grid-cols-3 gap-3">
      ${createMetric("Temperature", formatValue(weather.temperature, "°C"), "🌡️")}
      ${createMetric("Humidity", formatValue(weather.humidity, "%"), "💧")}
      ${createMetric("Wind", formatValue(weather.windSpeed, " km/h"), "💨")}
    </div>

    <div class="mt-3 flex items-center justify-between rounded-xl bg-slate-50 px-4 py-3 text-sm">
      <span class="text-slate-500">Weather code</span>
      <span class="font-semibold">${escapeHtml(weather.weatherCode ?? "—")}</span>
    </div>
  `;
}

function createMetric(label, value, icon) {
  return `
    <div class="rounded-2xl bg-slate-50 p-3 text-center">
      <div class="text-xl" aria-hidden="true">${icon}</div>
      <p class="mt-2 text-xs font-medium text-slate-500">${label}</p>
      <p class="mt-1 font-bold text-slate-900">${escapeHtml(value)}</p>
    </div>
  `;
}

function createSkeletonMetric() {
  return `
    <div class="animate-pulse rounded-2xl bg-slate-100 p-3">
      <div class="mx-auto h-5 w-5 rounded bg-slate-200"></div>
      <div class="mx-auto mt-3 h-3 w-16 rounded bg-slate-200"></div>
      <div class="mx-auto mt-2 h-5 w-12 rounded bg-slate-200"></div>
    </div>
  `;
}

function formatValue(value, suffix) {
  return value === undefined || value === null ? "—" : `${value}${suffix}`;
}

async function getErrorMessage(response) {
  try {
    const payload = await response.json();
    return payload.message || payload.error || `HTTP ${response.status}`;
  } catch {
    return `HTTP ${response.status}`;
  }
}

function setStatus(type, message) {
  const styles = {
    loading: "border-sky-200 bg-sky-50 text-sky-700",
    success: "border-emerald-200 bg-emerald-50 text-emerald-700",
    error: "border-rose-200 bg-rose-50 text-rose-700"
  };

  statusMessage.className = `mb-6 rounded-2xl border px-5 py-4 text-sm ${styles[type]}`;
  statusMessage.textContent = message;
  statusMessage.classList.remove("hidden");

  if (type === "success") {
    window.setTimeout(() => statusMessage.classList.add("hidden"), 2500);
  }
}

function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}
