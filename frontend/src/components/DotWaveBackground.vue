<script>
export default {
  name: 'DotWaveBackground',
}
</script>

<template>
  <div class="dot-wave" aria-hidden="true"></div>
</template>

<style scoped>
/* Lehe tausta täpivõrgu (theme.css .layout) kohal on teine, tumedam täpivõrk samas rütmis.
   Seda näidatakse ainult kitsa diagonaalse riba kaudu (mask), mis liigub ~4 sekundiga üle ekraani
   ja seisab ülejäänud minuti ekraanist väljas. Nii tunduvad täpid lainena tumenevat. */
.dot-wave {
  position: fixed;
  inset: 0;
  pointer-events: none;
  /* Sama ruudustik nagu .layout taustal: fixed element algab vaateakna nurgast, nii joonduvad täpid */
  background-image: radial-gradient(rgba(55, 71, 79, 0.14) 1px, transparent 1px);
  background-size: 22px 22px;
  -webkit-mask-image: linear-gradient(110deg, transparent 40%, #000 50%, transparent 60%);
  mask-image: linear-gradient(110deg, transparent 40%, #000 50%, transparent 60%);
  -webkit-mask-size: 300% 100%;
  mask-size: 300% 100%;
  -webkit-mask-repeat: no-repeat;
  mask-repeat: no-repeat;
  -webkit-mask-position: 100% 0;
  mask-position: 100% 0;
  /* Esimene laine 8 s pärast lehe avamist, seejärel kord minutis */
  animation: dot-wave 60s ease-in-out 8s infinite;
}

/* mask-position 100% → riba on ekraanist vasakul, 0% → paremal */
@keyframes dot-wave {
  0% {
    -webkit-mask-position: 100% 0;
    mask-position: 100% 0;
  }
  7% {
    -webkit-mask-position: 0 0;
    mask-position: 0 0;
  }
  100% {
    -webkit-mask-position: 0 0;
    mask-position: 0 0;
  }
}

@media (prefers-reduced-motion: reduce) {
  .dot-wave {
    display: none;
  }
}
</style>
