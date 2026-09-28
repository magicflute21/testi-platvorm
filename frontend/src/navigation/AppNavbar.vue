<script>
import AccountIcon from '@/navigation/AccountIcon.vue'

export default {
  name: 'AppNavbar',
  components: { AccountIcon: AccountIcon },
}
</script>

<template>
  <nav class="navbar navbar-expand-lg px-4 app-nav shadow-sm d-flex justify-content-between">
    <RouterLink class="navbar-brand brand" to="/">
      <span class="brand-mark" aria-hidden="true"></span>
      <span class="brand-skill">Skill</span><span class="brand-scope">Scope</span>
    </RouterLink>
    <AccountIcon />
  </nav>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@500;700&display=swap');

.app-nav {
  position: relative;
  overflow: hidden;
  background: #fff;
  border-bottom: 1px solid #eee;
}

.app-nav::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 360px;
  height: 100%;
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100' preserveAspectRatio='none'%3E%3Cpath d='M0 0 H40 Q52 45 85 100 H0 Z' fill='%23f1f1f4'/%3E%3C/svg%3E")
    no-repeat;
  background-size: 100% 100%;
  z-index: 0;
  pointer-events: none;
}

/* keep the nav content above the curve */
.app-nav > * {
  position: relative;
  z-index: 1;
}

.brand {
  display: inline-flex;
  align-items: center;
  gap: 0.2rem;
  font-family: 'Space Grotesk', system-ui, sans-serif;
  font-size: 1.5rem;
  letter-spacing: -0.03em;
  text-decoration: none;
}

.brand-skill {
  font-weight: 700;
  color: #1a1a2e;
}

.brand-scope {
  font-weight: 500;
  background: linear-gradient(90deg, #6c5ce7, #00b894);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

/* little "scope" target icon */
.brand-mark {
  width: 1.1rem;
  height: 1.1rem;
  border-radius: 50%;
  border: 3px solid #6c5ce7;
  position: relative;
}
.brand-mark::after {
  content: '';
  position: absolute;
  inset: 3px;
  border-radius: 50%;
  background: #00b894;
}

.brand:hover .brand-mark {
  transform: rotate(90deg) scale(1.1);
  transition: transform 0.25s ease;
}
</style>
