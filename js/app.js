(() => {
  "use strict";

  const KEYS = Object.freeze({
    USERS: "gs_users_v1",
    SESSION: "gs_session_v1",
    GAMES: "gs_games_v1",
    ORDERS_PREFIX: "gs_orders_v1_user_",
    CART_PREFIX: "gs_cart_v1_user_",
    LAST_SUCCESS_PREFIX: "gs_last_success_v1_user_",
  });

  const PAGES = Object.freeze({
    LOGIN: "login",
    HOME: "home",
    GAME: "game",
    CART: "cart",
    PAYMENT: "payment",
    ORDERS: "orders",
  });

  const IMG_VERSION = "2026-02-18-1";

  const $ = (sel, root = document) => root.querySelector(sel);
  const $$ = (sel, root = document) => Array.from(root.querySelectorAll(sel));

  function safeParse(json, fallback) {
    try {
      return JSON.parse(json);
    } catch {
      return fallback;
    }
  }

  const storage = {
    get(key, fallback) {
      const raw = localStorage.getItem(key);
      if (raw === null) return fallback;
      return safeParse(raw, fallback);
    },
    set(key, value) {
      localStorage.setItem(key, JSON.stringify(value));
    },
    del(key) {
      localStorage.removeItem(key);
    },
  };

  function money(n) {
    const v = Number(n || 0);
    return new Intl.NumberFormat(undefined, { style: "currency", currency: "USD" }).format(v);
  }

  function nowISO() {
    return new Date().toISOString();
  }

  function fmtDateTime(iso) {
    const d = new Date(iso);
    return d.toLocaleString(undefined, {
      year: "numeric",
      month: "short",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
    });
  }

  function uid(len = 10) {
    const a = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    let out = "";
    for (let i = 0; i < len; i++) out += a[Math.floor(Math.random() * a.length)];
    return out;
  }

  function toast(title, body, kind = "good", ms = 2600) {
    const host = $("#toasts");
    if (!host) return;
    const el = document.createElement("div");
    el.className = `toast ${kind}`;
    el.innerHTML = `
      <div class="dot" aria-hidden="true"></div>
      <div>
        <p class="t-title">${escapeHtml(title)}</p>
        <p class="t-body">${escapeHtml(body)}</p>
      </div>
    `;
    host.appendChild(el);
    window.setTimeout(() => {
      el.style.opacity = "0";
      el.style.transform = "translateY(6px)";
      window.setTimeout(() => el.remove(), 180);
    }, ms);
  }

  function escapeHtml(s) {
    return String(s)
      .replaceAll("&", "&amp;")
      .replaceAll("<", "&lt;")
      .replaceAll(">", "&gt;")
      .replaceAll('"', "&quot;")
      .replaceAll("'", "&#039;");
  }

  function getPage() {
    return (document.body?.dataset?.page || "").trim();
  }

  function getSession() {
    return storage.get(KEYS.SESSION, null);
  }

  function setSession(session) {
    storage.set(KEYS.SESSION, session);
  }

  function clearSession() {
    storage.del(KEYS.SESSION);
  }

  function requireAuth() {
    const session = getSession();
    const page = getPage();
    if (page === PAGES.LOGIN) {
      if (session?.userId) window.location.replace("./index.html");
      return;
    }
    // Non-login pages are public; page-level actions still check for session where needed.
  }

  function getUsers() {
    return storage.get(KEYS.USERS, []);
  }

  function setUsers(users) {
    storage.set(KEYS.USERS, users);
  }

  function cartKey(userId) {
    return `${KEYS.CART_PREFIX}${userId}`;
  }

  function ordersKey(userId) {
    return `${KEYS.ORDERS_PREFIX}${userId}`;
  }

  function lastSuccessKey(userId) {
    return `${KEYS.LAST_SUCCESS_PREFIX}${userId}`;
  }

  function getCart(userId) {
    return storage.get(cartKey(userId), []);
  }

  function setCart(userId, cart) {
    storage.set(cartKey(userId), cart);
  }

  function getOrders(userId) {
    return storage.get(ordersKey(userId), []);
  }

  function setOrders(userId, orders) {
    storage.set(ordersKey(userId), orders);
  }

  function getGames() {
    const existing = storage.get(KEYS.GAMES, null);
    if (Array.isArray(existing) && existing.length) return existing;

    const seeded = seedGames();
    storage.set(KEYS.GAMES, seeded);
    return seeded;
  }

  function seedGames() {
    // Images are lightweight SVG data URIs (instant, offline, Lighthouse-friendly).
    return [
      {
        id: "neon-drift",
        title: "Neon Drift",
        price: 19.99,
        genre: "Racing",
        rating: 4.6,
        accentA: "#7C4DFF",
        accentB: "#00D4FF",
        description:
          "A high-speed synthwave racer with tight drifting, time trials, and neon cityscapes. Master boost chains and dominate leaderboards.",
      },
      {
        id: "iron-legion",
        title: "Iron Legion",
        price: 29.99,
        genre: "Action",
        rating: 4.4,
        accentA: "#FF4D6D",
        accentB: "#FFB020",
        description:
          "Build your loadout, upgrade exo-gear, and fight through cinematic missions. Fast combat, satisfying progression, and co-op arenas.",
      },
      {
        id: "void-echo",
        title: "Void Echo",
        price: 24.99,
        genre: "Sci‑Fi RPG",
        rating: 4.7,
        accentA: "#00D4FF",
        accentB: "#35D07F",
        description:
          "A story-driven space RPG with branching choices and crew management. Explore derelict stations, negotiate alliances, and shape the galaxy.",
      },
      {
        id: "cryptkeeper",
        title: "Cryptkeeper",
        price: 14.99,
        genre: "Roguelite",
        rating: 4.3,
        accentA: "#35D07F",
        accentB: "#7C4DFF",
        description:
          "A roguelite dungeon crawler with bite-sized runs and deep builds. Combine relics, unlock characters, and break the curse run by run.",
      },
      {
        id: "skyforge-tactics",
        title: "Skyforge Tactics",
        price: 34.99,
        genre: "Strategy",
        rating: 4.2,
        accentA: "#FFB020",
        accentB: "#7C4DFF",
        description:
          "Turn-based tactical battles above the clouds. Command squads, manage resources, and adapt to dynamic weather and terrain.",
      },
      {
        id: "shadow-circuit",
        title: "Shadow Circuit",
        price: 21.99,
        genre: "Stealth",
        rating: 4.5,
        accentA: "#7C4DFF",
        accentB: "#FF4D6D",
        description:
          "Infiltrate megacorp facilities using gadgets, disguises, and silent takedowns. Every mission supports multiple playstyles and routes.",
      },
      {
        id: "astral-odyssey",
        title: "Astral Odyssey",
        price: 39.99,
        genre: "Open World",
        rating: 4.8,
        accentA: "#00D4FF",
        accentB: "#7C4DFF",
        description:
          "A massive open-world adventure across floating islands and ancient ruins. Glide, craft, and uncover secrets hidden in the skies.",
      },
      {
        id: "pixel-quest",
        title: "Pixel Quest DX",
        price: 9.99,
        genre: "Indie",
        rating: 4.1,
        accentA: "#35D07F",
        accentB: "#00D4FF",
        description:
          "A cozy retro platformer with crisp controls and clever secrets. Perfect for quick sessions and completionists alike.",
      },
      {
        id: "mecha-arena",
        title: "Mecha Arena",
        price: 27.99,
        genre: "Shooter",
        rating: 4.0,
        accentA: "#FF4D6D",
        accentB: "#00D4FF",
        description:
          "Fast-paced mech shooter with customizable weapons and maps designed for smart flanks. Play solo modes or team skirmishes.",
      },
    ];
  }

  function gameImageDataUri(title, a, b) {
    // Keep SVG tiny; encodeURIComponent for safe data URI.
    const t = title.length > 18 ? `${title.slice(0, 18)}…` : title;
    const svg = `
        <defs>
          <linearGradient id="g" x1="0" y1="0" x2="1" y2="1">
            <stop offset="0" stop-color="${a}"/>
            <stop offset="1" stop-color="${b}"/>
          </linearGradient>
          <radialGradient id="r" cx="30%" cy="25%" r="75%">
            <stop offset="0" stop-color="rgba(255,255,255,.22)"/>
            <stop offset="1" stop-color="rgba(255,255,255,0)"/>
          </radialGradient>
          <filter id="n" x="-20%" y="-20%" width="140%" height="140%">
            <feTurbulence type="fractalNoise" baseFrequency=".9" numOctaves="2" stitchTiles="stitch"/>
            <feColorMatrix type="matrix" values="
              0 0 0 0 0
              0 0 0 0 0
              0 0 0 0 0
              0 0 0 .18 0"/>
          </filter>
        </defs>
        <rect width="960" height="600" fill="#0B1020"/>
        <rect width="960" height="600" fill="url(#g)" opacity=".85"/>
        <rect width="960" height="600" fill="url(#r)"/>
        <rect width="960" height="600" filter="url(#n)" opacity=".65"/>
        <g opacity=".85">
          <path d="M70 430 C220 300, 340 520, 520 420 S 820 380, 900 260" fill="none" stroke="rgba(255,255,255,.22)" stroke-width="10"/>
          <path d="M60 510 C250 380, 420 560, 600 470 S 840 460, 930 330" fill="none" stroke="rgba(0,0,0,.25)" stroke-width="14"/>
        </g>
        <g>
          <text x="56" y="88" fill="rgba(255,255,255,.92)" font-family="ui-sans-serif,system-ui" font-weight="800" font-size="44">${escapeXml(t)}</text>
          <text x="58" y="126" fill="rgba(255,255,255,.75)" font-family="ui-monospace,monospace" font-weight="700" font-size="18">GAME STORE • DEMO ART</text>
        </g>
      </svg>
    `.trim();
    return `data:image/svg+xml;charset=utf-8,${encodeURIComponent(svg)}`;
  }

  function gameImageFileUrlById(id) {
    // Store game images in /resources/<game-id>.svg
    const safe = String(id || "").trim();
    return `./resources/${encodeURIComponent(safe)}.svg?v=${encodeURIComponent(IMG_VERSION)}`;
  }

  function escapeXml(s) {
    return String(s)
      .replaceAll("&", "&amp;")
      .replaceAll("<", "&lt;")
      .replaceAll(">", "&gt;")
      .replaceAll('"', "&quot;")
      .replaceAll("'", "&apos;");
  }

  async function sha256Hex(text) {
    if (window.crypto?.subtle?.digest) {
      const bytes = new TextEncoder().encode(text);
      const buf = await crypto.subtle.digest("SHA-256", bytes);
      return Array.from(new Uint8Array(buf))
        .map((b) => b.toString(16).padStart(2, "0"))
        .join("");
    }
    // Fallback (not cryptographically secure)
    let h = 5381;
    for (let i = 0; i < text.length; i++) h = (h * 33) ^ text.charCodeAt(i);
    return `fallback_${(h >>> 0).toString(16)}`;
  }

  function isEmail(s) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(String(s || "").trim());
  }

  function navbar() {
    const host = $("#nav");
    if (!host) return;
    const page = getPage();
    const session = getSession();
    const cartCount = session?.userId ? getCart(session.userId).reduce((a, it) => a + (Number(it.qty) || 0), 0) : 0;

    host.innerHTML = `
      <div class="nav-inner">
        <a class="brand" href="./index.html" aria-label="Game Store home">
          <span class="brand-badge" aria-hidden="true"></span>
          <span>Game Store</span>
        </a>
        <nav class="nav-links" aria-label="Primary">
          <a class="nav-link" href="./index.html" data-nav="home">Home</a>
          <a class="nav-link" href="./cart.html" data-nav="cart">Cart <span class="badge" id="cart-badge">${cartCount}</span></a>
          <a class="nav-link" href="./orders.html" data-nav="orders">Orders</a>
          <button class="nav-link btn-ghost" type="button" id="logout">Logout</button>
        </nav>
        <div class="nav-actions">
          <span class="nav-user" id="nav-user"></span>
        </div>
      </div>
    `;

    // Highlight current
    const map = { [PAGES.HOME]: "home", [PAGES.CART]: "cart", [PAGES.ORDERS]: "orders", [PAGES.GAME]: "home", [PAGES.PAYMENT]: "cart" };
    const active = map[page];
    if (active) {
      const el = host.querySelector(`[data-nav="${active}"]`);
      if (el) {
        el.style.background = "rgba(124,77,255,.14)";
        el.style.borderColor = "rgba(124,77,255,.45)";
        el.style.color = "var(--text)";
      }
    }

    const navUser = $("#nav-user");
    if (navUser && session?.name) {
      navUser.textContent = `Signed in as ${session.name}`;
      navUser.style.display = "inline";
    }

    const btn = $("#logout");
    if (btn) {
      btn.addEventListener("click", () => {
        clearSession();
        toast("Logged out", "See you next time.", "good");
        window.setTimeout(() => window.location.replace("./login.html"), 350);
      });
    }
  }

  function setAuthTabs() {
    const tabLogin = $("#tab-login");
    const tabSignup = $("#tab-signup");
    const loginForm = $("#login-form");
    const signupForm = $("#signup-form");
    if (!tabLogin || !tabSignup || !loginForm || !signupForm) return;

    const show = (which) => {
      const isLogin = which === "login";
      tabLogin.setAttribute("aria-selected", String(isLogin));
      tabSignup.setAttribute("aria-selected", String(!isLogin));
      loginForm.style.display = isLogin ? "block" : "none";
      signupForm.style.display = isLogin ? "none" : "block";
      const msg = isLogin ? $("#signup-msg") : $("#login-msg");
      if (msg) msg.style.display = "none";
    };

    tabLogin.addEventListener("click", () => show("login"));
    tabSignup.addEventListener("click", () => show("signup"));
  }

  function setMsg(id, text) {
    const el = $(id);
    if (!el) return;
    el.textContent = text;
    el.style.display = text ? "block" : "none";
  }

  function authHandlers() {
    const loginForm = $("#login-form");
    const signupForm = $("#signup-form");
    if (loginForm) {
      loginForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        setMsg("#login-msg", "");

        const email = String($("#login-email")?.value || "").trim().toLowerCase();
        const password = String($("#login-password")?.value || "");
        if (!isEmail(email) || password.length < 1) {
          setMsg("#login-msg", "Please enter a valid email and password.");
          return;
        }

        const users = getUsers();
        const user = users.find((u) => u.email === email);
        if (!user) {
          setMsg("#login-msg", "Account not found. Please signup first.");
          return;
        }

        const hash = await sha256Hex(password);
        if (hash !== user.passwordHash) {
          setMsg("#login-msg", "Invalid password. Please try again.");
          return;
        }

        setSession({
          userId: user.id,
          email: user.email,
          name: user.name,
          token: `demo_${uid(14)}`,
          createdAt: nowISO(),
        });

        toast("Welcome back", `Signed in as ${user.name}.`, "good");
        window.setTimeout(() => window.location.replace("./index.html"), 450);
      });
    }

    if (signupForm) {
      signupForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        setMsg("#signup-msg", "");

        const name = String($("#signup-name")?.value || "").trim();
        const email = String($("#signup-email")?.value || "").trim().toLowerCase();
        const password = String($("#signup-password")?.value || "");

        if (name.length < 2) {
          setMsg("#signup-msg", "Please enter a display name (at least 2 characters).");
          return;
        }
        if (!isEmail(email)) {
          setMsg("#signup-msg", "Please enter a valid email address.");
          return;
        }
        if (password.length < 8) {
          setMsg("#signup-msg", "Password must be at least 8 characters.");
          return;
        }

        const users = getUsers();
        if (users.some((u) => u.email === email)) {
          setMsg("#signup-msg", "That email is already registered. Please login.");
          return;
        }

        const user = {
          id: `u_${uid(12)}`,
          name,
          email,
          passwordHash: await sha256Hex(password),
          createdAt: nowISO(),
        };
        users.push(user);
        setUsers(users);

        setSession({
          userId: user.id,
          email: user.email,
          name: user.name,
          token: `demo_${uid(14)}`,
          createdAt: nowISO(),
        });

        toast("Account created", `Welcome, ${user.name}!`, "good");
        window.setTimeout(() => window.location.replace("./index.html"), 450);
      });
    }
  }

  function renderGames(games, query = "") {
    const grid = $("#games-grid");
    const empty = $("#empty");
    if (!grid) return;

    const q = String(query || "").trim().toLowerCase();
    const list = q ? games.filter((g) => g.title.toLowerCase().includes(q)) : games;

    grid.innerHTML = list
      .map((g) => {
        const img = gameImageFileUrlById(g.id);
        const fallback = gameImageDataUri(g.title, g.accentA, g.accentB);
        const rating = typeof g.rating === "number" ? `${g.rating.toFixed(1)}★` : "—";
        return `
          <article class="game-card" aria-label="${escapeHtml(g.title)}">
            <div class="thumb">
              <img src="${img}" alt="${escapeHtml(g.title)} cover art" loading="lazy" decoding="async"
                onerror="this.onerror=null;this.src='${fallback}'" />
            </div>
            <div class="body">
              <div class="row">
                <h2 class="title">${escapeHtml(g.title)}</h2>
                <div class="price">${escapeHtml(money(g.price))}</div>
              </div>
              <div class="meta">
                <span class="pill">${escapeHtml(g.genre)}</span>
                <span class="pill">Rating: ${escapeHtml(rating)}</span>
              </div>
              <div class="card-actions">
                <a class="btn btn-primary" href="./game.html?id=${encodeURIComponent(g.id)}">View details</a>
              </div>
            </div>
          </article>
        `;
      })
      .join("");

    if (empty) empty.style.display = list.length ? "none" : "block";
  }

  function homePage() {
    const games = getGames();
    const input = $("#search");
    const reset = $("#reset-search");
    renderGames(games, "");

    if (input) {
      let t = 0;
      input.addEventListener("input", () => {
        // small debounce for smoother typing
        window.clearTimeout(t);
        t = window.setTimeout(() => renderGames(games, input.value), 60);
      });
    }
    if (reset && input) {
      reset.addEventListener("click", () => {
        input.value = "";
        renderGames(games, "");
        input.focus();
      });
    }
  }

  function findGame(id) {
    const games = getGames();
    return games.find((g) => g.id === id) || null;
  }

  function gamePage() {
    const wrap = $("#game-wrap");
    const notFound = $("#not-found");
    if (!wrap) return;

    const params = new URLSearchParams(window.location.search);
    const id = params.get("id") || "";
    const g = findGame(id);
    if (!g) {
      wrap.innerHTML = "";
      if (notFound) notFound.style.display = "block";
      return;
    }

    document.title = `Game Store • ${g.title}`;
    const img = gameImageFileUrlById(g.id);
    const fallback = gameImageDataUri(g.title, g.accentA, g.accentB);
    const rating = typeof g.rating === "number" ? `${g.rating.toFixed(1)}★` : "—";

    wrap.innerHTML = `
      <section class="hero-img" aria-label="Game artwork">
        <img src="${img}" alt="${escapeHtml(g.title)} cover art" decoding="async"
          onerror="this.onerror=null;this.src='${fallback}'" />
      </section>
      <section class="card card-pad" aria-label="Game info">
        <h1 style="margin:0; font-size: 26px;">${escapeHtml(g.title)}</h1>
        <div class="meta" style="margin-top: 10px;">
          <span class="pill">${escapeHtml(g.genre)}</span>
          <span class="pill">Rating: ${escapeHtml(rating)}</span>
          <span class="pill">Price: <span class="price">${escapeHtml(money(g.price))}</span></span>
        </div>
        <p class="desc">${escapeHtml(g.description)}</p>
        <div class="kv" aria-label="Quick stats">
          <div class="kpi"><b>${escapeHtml(money(g.price))}</b><span>One-time purchase</span></div>
          <div class="kpi"><b>${escapeHtml(rating)}</b><span>Community rating</span></div>
        </div>
        <div style="display:flex; gap: 10px; flex-wrap: wrap; margin-top: 14px;">
          <button class="btn btn-primary" id="add-to-cart" type="button">Add to cart</button>
          <a class="btn btn-ghost" href="./index.html">Back to Home</a>
        </div>
      </section>
    `;

    const btn = $("#add-to-cart");
    const session = getSession();
    if (btn && session?.userId) {
      btn.addEventListener("click", () => {
        const cart = getCart(session.userId);
        const existing = cart.find((it) => it.gameId === g.id);
        if (existing) existing.qty = Math.min(99, (Number(existing.qty) || 1) + 1);
        else cart.push({ gameId: g.id, qty: 1, addedAt: nowISO() });
        setCart(session.userId, cart);
        navbar();
        toast("Added to cart", `${g.title} is ready to checkout.`, "good");
      });
    }
  }

  function cartTotals(cart, gamesById) {
    const lines = cart
      .map((it) => {
        const g = gamesById.get(it.gameId);
        if (!g) return null;
        const qty = Math.max(1, Math.min(99, Number(it.qty) || 1));
        const lineTotal = qty * Number(g.price || 0);
        return { ...it, qty, title: g.title, price: Number(g.price || 0), genre: g.genre, rating: g.rating, lineTotal, accentA: g.accentA, accentB: g.accentB };
      })
      .filter(Boolean);
    const total = lines.reduce((a, l) => a + l.lineTotal, 0);
    return { lines, total };
  }

  function cartPage() {
    const listEl = $("#cart-list");
    const footer = $("#cart-footer");
    const empty = $("#empty-cart");
    const totalEl = $("#grand-total");
    const checkoutBtn = $("#checkout");

    const session = getSession();
    if (!session?.userId || !listEl) return;

    const games = getGames();
    const gamesById = new Map(games.map((g) => [g.id, g]));

    const render = () => {
      const cart = getCart(session.userId);
      const { lines, total } = cartTotals(cart, gamesById);

      if (!lines.length) {
        listEl.innerHTML = "";
        if (footer) footer.style.display = "none";
        if (empty) empty.style.display = "block";
        navbar();
        return;
      }

      if (empty) empty.style.display = "none";
      if (footer) footer.style.display = "block";
      if (totalEl) totalEl.textContent = money(total);

      listEl.innerHTML = lines
        .map((l) => {
          const img = gameImageFileUrlById(l.gameId);
          const fallback = gameImageDataUri(l.title, l.accentA, l.accentB);
          return `
            <div class="cart-item" data-id="${escapeHtml(l.gameId)}">
              <img src="${img}" alt="${escapeHtml(l.title)} cover art" loading="lazy" decoding="async"
                onerror="this.onerror=null;this.src='${fallback}'" />
              <div>
                <h3>${escapeHtml(l.title)}</h3>
                <div class="muted">${escapeHtml(money(l.price))} • Qty ${escapeHtml(String(l.qty))} • Item total: <span class="price">${escapeHtml(money(l.lineTotal))}</span></div>
              </div>
              <div class="qty" aria-label="Quantity controls">
                <button class="btn icon-btn" data-act="dec" type="button" aria-label="Decrease quantity">−</button>
                <div class="count" aria-label="Quantity">${escapeHtml(String(l.qty))}</div>
                <button class="btn icon-btn" data-act="inc" type="button" aria-label="Increase quantity">+</button>
                <button class="btn btn-danger" data-act="rm" type="button">Remove</button>
              </div>
            </div>
          `;
        })
        .join("");

      navbar();
    };

    listEl.addEventListener("click", (e) => {
      const btn = e.target?.closest?.("button[data-act]");
      if (!btn) return;
      const row = btn.closest(".cart-item");
      const gameId = row?.getAttribute("data-id");
      if (!gameId) return;

      const act = btn.getAttribute("data-act");
      const cart = getCart(session.userId);
      const idx = cart.findIndex((it) => it.gameId === gameId);
      if (idx === -1) return;

      if (act === "inc") cart[idx].qty = Math.min(99, (Number(cart[idx].qty) || 1) + 1);
      if (act === "dec") cart[idx].qty = Math.max(1, (Number(cart[idx].qty) || 1) - 1);
      if (act === "rm") cart.splice(idx, 1);

      setCart(session.userId, cart);
      render();
    });

    if (checkoutBtn) {
      checkoutBtn.addEventListener("click", () => {
        const cart = getCart(session.userId);
        if (!cart.length) {
          setMsg("#cart-msg", "Your cart is empty.");
          return;
        }
        window.location.href = "./payment.html";
      });
    }

    render();
  }

  function normalizeCardNumber(raw) {
    return String(raw || "").replaceAll(/\D+/g, "");
  }

  function luhnCheck(numStr) {
    let sum = 0;
    let alt = false;
    for (let i = numStr.length - 1; i >= 0; i--) {
      let n = numStr.charCodeAt(i) - 48;
      if (n < 0 || n > 9) return false;
      if (alt) {
        n *= 2;
        if (n > 9) n -= 9;
      }
      sum += n;
      alt = !alt;
    }
    return sum % 10 === 0;
  }

  function parseExp(mmYY) {
    const s = String(mmYY || "").trim();
    const m = s.match(/^(\d{2})\s*\/\s*(\d{2})$/);
    if (!m) return null;
    const mm = Number(m[1]);
    const yy = Number(m[2]);
    if (mm < 1 || mm > 12) return null;
    const year = 2000 + yy;
    return { mm, year };
  }

  function isExpValid(exp) {
    const p = parseExp(exp);
    if (!p) return false;
    const end = new Date(p.year, p.mm, 0, 23, 59, 59, 999); // last day of that month
    return end.getTime() >= Date.now();
  }

  function paymentPage() {
    const session = getSession();
    if (!session?.userId) return;

    const cart = getCart(session.userId);
    const noCart = $("#no-cart");
    const itemsEl = $("#order-items");
    const totalEl = $("#order-total");
    const form = $("#payment-form");

    if (!cart.length) {
      if (noCart) noCart.style.display = "block";
      if (form) form.style.display = "none";
      return;
    }

    const games = getGames();
    const gamesById = new Map(games.map((g) => [g.id, g]));
    const { lines, total } = cartTotals(cart, gamesById);

    if (itemsEl) {
      itemsEl.innerHTML = lines
        .map((l) => `<div class="order-item"><b>${escapeHtml(l.title)}</b><span>${escapeHtml(String(l.qty))} × ${escapeHtml(money(l.price))}</span></div>`)
        .join("");
    }
    if (totalEl) totalEl.textContent = money(total);

    // Input helpers
    const cardNumber = $("#card-number");
    const exp = $("#exp");
    const cvv = $("#cvv");
    if (cardNumber) {
      cardNumber.addEventListener("input", () => {
        const digits = normalizeCardNumber(cardNumber.value).slice(0, 19);
        const grouped = digits.replaceAll(/(.{4})/g, "$1 ").trim();
        cardNumber.value = grouped;
      });
    }
    if (exp) {
      exp.addEventListener("input", () => {
        const digits = String(exp.value || "").replaceAll(/\D+/g, "").slice(0, 4);
        if (digits.length <= 2) exp.value = digits;
        else exp.value = `${digits.slice(0, 2)}/${digits.slice(2)}`;
      });
    }
    if (cvv) {
      cvv.addEventListener("input", () => {
        cvv.value = String(cvv.value || "").replaceAll(/\D+/g, "").slice(0, 4);
      });
    }

    if (!form) return;
    form.addEventListener("submit", async (e) => {
      e.preventDefault();
      setMsg("#pay-msg", "");

      const cardName = String($("#card-name")?.value || "").trim();
      const cardNum = normalizeCardNumber($("#card-number")?.value || "");
      const expVal = String($("#exp")?.value || "").trim();
      const cvvVal = String($("#cvv")?.value || "").trim();
      const billing = String($("#billing")?.value || "").trim();
      const city = String($("#city")?.value || "").trim();
      const zip = String($("#zip")?.value || "").trim();
      const country = String($("#country")?.value || "").trim();

      if (cardName.length < 2) return setMsg("#pay-msg", "Please enter the cardholder name.");
      if (cardNum.length < 12 || cardNum.length > 19 || !luhnCheck(cardNum)) return setMsg("#pay-msg", "Please enter a valid card number.");
      if (!isExpValid(expVal)) return setMsg("#pay-msg", "Please enter a valid expiry date (MM/YY).");
      if (!/^\d{3,4}$/.test(cvvVal)) return setMsg("#pay-msg", "Please enter a valid CVV (3–4 digits).");
      if (billing.length < 5) return setMsg("#pay-msg", "Please enter your billing address.");
      if (city.length < 2) return setMsg("#pay-msg", "Please enter your city.");
      if (zip.length < 3) return setMsg("#pay-msg", "Please enter your ZIP / postal code.");
      if (!country) return setMsg("#pay-msg", "Please select your country.");

      const payBtn = $("#pay-btn");
      if (payBtn) {
        payBtn.disabled = true;
        payBtn.textContent = "Processing…";
      }

      // Simulate payment processing delay
      await new Promise((r) => window.setTimeout(r, 850));

      const orderId = `GS-${new Date().toISOString().slice(0, 10).replaceAll("-", "")}-${uid(6)}`;
      const createdAt = nowISO();
      const order = {
        orderId,
        createdAt,
        total: Number(total.toFixed(2)),
        items: lines.map((l) => ({
          gameId: l.gameId,
          title: l.title,
          price: l.price,
          qty: l.qty,
        })),
        payment: {
          method: "card",
          last4: cardNum.slice(-4),
          cardholder: cardName,
          billingCity: city,
          billingCountry: country,
        },
      };

      const orders = getOrders(session.userId);
      orders.unshift(order);
      setOrders(session.userId, orders);
      setCart(session.userId, []);
      storage.set(lastSuccessKey(session.userId), {
        orderId,
        createdAt,
        total: order.total,
      });

      toast("Payment successful", `Order ${orderId} created.`, "good");
      window.setTimeout(() => window.location.replace("./orders.html?success=1"), 500);
    });
  }

  function ordersPage() {
    const session = getSession();
    if (!session?.userId) return;

    const list = $("#orders");
    const none = $("#no-orders");
    if (!list) return;

    const params = new URLSearchParams(window.location.search);
    const success = params.get("success") === "1";
    if (success) {
      const info = storage.get(lastSuccessKey(session.userId), null);
      const box = $("#orders-success");
      const msg = $("#orders-success-msg");
      if (box && msg && info?.orderId) {
        box.style.display = "block";
        msg.textContent = `Order ${info.orderId} • ${fmtDateTime(info.createdAt)} • Total ${money(info.total)}`;
        storage.del(lastSuccessKey(session.userId));
      }
    }

    const orders = getOrders(session.userId);
    if (!orders.length) {
      list.innerHTML = "";
      if (none) none.style.display = "block";
      return;
    }
    if (none) none.style.display = "none";

    list.innerHTML = orders
      .map((o) => {
        const items = o.items
          .map((it) => `<div class="order-item"><b>${escapeHtml(it.title)}</b><span>${escapeHtml(String(it.qty))} × ${escapeHtml(money(it.price))}</span></div>`)
          .join("");
        return `
          <article class="order" aria-label="Order ${escapeHtml(o.orderId)}">
            <div class="order-head">
              <div>
                <div class="order-id">${escapeHtml(o.orderId)}</div>
                <div class="order-meta">Purchased: ${escapeHtml(fmtDateTime(o.createdAt))}</div>
              </div>
              <div class="order-meta"><span class="price">${escapeHtml(money(o.total))}</span></div>
            </div>
            <div class="order-items">${items}</div>
          </article>
        `;
      })
      .join("");
  }

  function init() {
    requireAuth();
    const page = getPage();

    if (page !== PAGES.LOGIN) navbar();

    if (page === PAGES.LOGIN) {
      setAuthTabs();
      authHandlers();
      getGames(); // seed catalog early
    }
    if (page === PAGES.HOME) homePage();
    if (page === PAGES.GAME) gamePage();
    if (page === PAGES.CART) cartPage();
    if (page === PAGES.PAYMENT) paymentPage();
    if (page === PAGES.ORDERS) ordersPage();
  }

  init();
})();


