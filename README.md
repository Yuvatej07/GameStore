# Game Store Website (Vanilla JS + LocalStorage)

Static multi-page demo store with:
- Login/Signup (LocalStorage)
- Home (search + grid)
- Game details (add to cart)
- Cart (quantity + totals)
- Payment (validation + simulated processing)
- Orders (purchase history)

## Run locally

### Option A: Python (recommended)

From the `GameStoreWebsite` folder:

```bash
python -m http.server 5500
```

Open `http://localhost:5500/login.html`

### Option B: VS Code Live Server

Open the folder and run Live Server on `login.html`.

## Notes
- This is a **client-only demo**. Do not use for real payments or real authentication.
- Passwords are stored as a hash in LocalStorage.
