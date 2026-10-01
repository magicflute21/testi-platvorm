<script>
import AccountIcon from '@/navigation/AccountIcon.vue'

export default {
  name: 'AppNavbar',
  components: { AccountIcon: AccountIcon },
  props: {
    showAccountIcon: {
      type: Boolean,
      default: true,
    },
  },
}
</script>

<template>
  <nav class="navbar navbar-expand-lg px-4 app-nav shadow-sm d-flex justify-content-between">
    <RouterLink class="navbar-brand brand" to="/">
      <span class="brand-mark" aria-hidden="true"></span>
      <span class="brand-skill">Skill</span><span class="brand-scope">Scope</span>
    </RouterLink>
    <AccountIcon v-if="showAccountIcon" />
  </nav>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Sora:wght@700;800&display=swap');

.app-nav {
  position: relative;
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
  /* Sama täpivõrk ja alatoon nagu lehe taustal (theme.css .layout) —
     fixed-kinnitusega joonduvad täpid lehe taustaga. Kaare kuju tuleb maskist. */
  background-color: #e4ede7;
  background-image: radial-gradient(rgba(55, 71, 79, 0.12) 1px, transparent 1px);
  background-size: 22px 22px;
  background-attachment: fixed;
  -webkit-mask: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100' preserveAspectRatio='none'%3E%3Cpath d='M0 0 H40 Q52 45 85 100 H0 Z' fill='black'/%3E%3C/svg%3E")
    no-repeat 0 0 / 100% 100%;
  mask: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100' preserveAspectRatio='none'%3E%3Cpath d='M0 0 H40 Q52 45 85 100 H0 Z' fill='black'/%3E%3C/svg%3E")
    no-repeat 0 0 / 100% 100%;
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
  font-family: 'Sora', system-ui, sans-serif;
  font-size: 1.6rem;
  letter-spacing: -0.06em;
  text-decoration: none;
}

.brand-skill {
  font-weight: 800;
  color: #1a1a2e;
}

/* Rohelised toonid on tuletatud aaloe illustratsiooni mündirohelisest (#92e3a9) */
.brand-scope {
  font-weight: 800;
  background: linear-gradient(90deg, #1f8a4c, #3fbf74);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

/* little "scope" target icon */
.brand-mark {
  width: 1.1rem;
  height: 1.1rem;
  border-radius: 50%;
  margin-right: 0.35rem;
  border: 3px solid #1f8a4c;
  position: relative;
}
.brand-mark::after {
  content: '';
  position: absolute;
  inset: 3px;
  border-radius: 50%;
  background: #92e3a9;
}

.brand:hover .brand-mark {
  transform: rotate(90deg) scale(1.1);
  transition: transform 0.25s ease;
}
</style>
