<script>
import { PhCaretDown, PhChartBar, PhDotsThree, PhStar } from '@phosphor-icons/vue'

export default {
  name: 'QuestionBankCard',
  components: { PhCaretDown, PhChartBar, PhDotsThree, PhStar },
  props: {
    statusBadge: { type: Object, default: null },
    competenceName: { type: String, default: '' },
    questionTypeLabel: { type: String, default: '' },
    competenceLevelName: { type: String, default: '' },
    score: { type: Number, default: null },
  },
  data() {
    return {
      isOpen: false,
    }
  },
  methods: {
    toggleOpen() {
      this.isOpen = !this.isOpen
    },
  },
}
</script>

<template>
  <div class="card question-card shadow rounded-4">
    <div class="card-body question-card-body">
      <div class="d-flex align-items-start gap-3 question-card-header" @click="toggleOpen">
        <div class="question-card-heading">
          <h5 class="card-title mb-0">
            <slot name="title"></slot>
          </h5>
          <span v-if="questionTypeLabel" class="header-row flex-shrink-0">
            <span class="badge rounded-pill badge-neutral">{{ questionTypeLabel }}</span>
          </span>
          <div class="header-row header-badges">
            <span v-if="competenceName" class="badge rounded-pill badge-competence">
              {{ competenceName }}
            </span>
            <span v-if="statusBadge" class="badge rounded-pill" :class="statusBadge.badgeClass">{{
              statusBadge.name
            }}</span>
          </div>
        </div>
        <div v-if="$slots.menu" class="header-row dropdown flex-shrink-0" @click.stop>
          <button class="btn btn-sm btn-outline-secondary py-0 px-1" data-bs-toggle="dropdown">
            <PhDotsThree :size="18" />
          </button>
          <ul class="dropdown-menu dropdown-menu-custom dropdown-menu-end">
            <slot name="menu"></slot>
          </ul>
        </div>
        <button
          class="header-row btn btn-sm p-0 flex-shrink-0"
          :aria-expanded="isOpen"
          aria-label="Ava või sulge küsimus"
          @click.stop="toggleOpen"
        >
          <PhCaretDown :size="20" class="caret" :class="{ open: isOpen }" />
        </button>
      </div>

      <div v-if="isOpen" class="question-card-content mt-4 pt-4">
        <div class="d-flex flex-wrap gap-2 mb-4">
          <span v-if="competenceLevelName" class="badge rounded-pill badge-neutral badge-icon">
            <PhChartBar :size="12" weight="bold" />
            Tase: {{ competenceLevelName }}
          </span>
          <span v-if="score !== null" class="badge rounded-pill badge-neutral badge-icon">
            <PhStar :size="12" weight="bold" />
            Punktid: {{ score }}
          </span>
        </div>

        <p class="card-text">
          <slot name="description"></slot>
        </p>

        <h6 class="section-label text-secondary text-uppercase fw-semibold mt-4 mb-3">
          Vastusevariandid
        </h6>
        <div class="answers-grid">
          <slot name="answers"></slot>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.question-card {
  /* Paigutus muutub kaardi enda laiuse järgi, mitte akna laiuse järgi (külgmenüü võtab osa ruumist) */
  container: question-card / inline-size;
  min-width: 0;
  transition: box-shadow 0.15s ease;
}

.question-card:hover {
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
}

.question-card-header {
  cursor: pointer;
  min-width: 0;
}

.question-card-body {
  padding: 1.75rem 2rem;
}

@container question-card (min-width: 800px) {
  .question-card-body {
    padding: 2rem 3.5rem;
  }
}

@container question-card (max-width: 599.98px) {
  .question-card-body {
    padding: 1.5rem 1.5rem;
  }
}

.question-card-header {
  --header-row-height: 1.875rem;
}

/* Pealkiri, tüübimull ja kompetentsi/staatuse mullid ühes plokis */
.question-card-heading {
  flex: 1 1 0;
  min-width: 0;
  display: flex;
  align-items: flex-start;
  column-gap: 1rem;
  row-gap: 0.5rem;
}

/* Pealkirja esimene rida ja kõik mullid/nupud on sama kõrgusega, et need joonduksid ülevalt */
.question-card-heading .card-title {
  flex: 0 1 auto;
  min-width: 0;
  line-height: var(--header-row-height);
}

.header-badges {
  margin-left: auto;
  flex-shrink: 0;
  gap: 0.5rem;
}

/* Kitsal kaardil liiguvad kõik mullid pealkirja alla */
@container question-card (max-width: 599.98px) {
  .question-card-heading {
    flex-wrap: wrap;
    column-gap: 0.5rem;
  }

  .question-card-heading .card-title {
    flex-basis: 100%;
  }

  .header-badges {
    margin-left: 0;
  }
}

.header-row {
  min-height: var(--header-row-height);
  display: inline-flex;
  align-items: center;
}

.card-title,
.card-text {
  overflow-wrap: anywhere;
}

.card-text {
  white-space: pre-line;
  line-height: 1.6;
}

.question-card-content {
  border-top: 1px solid var(--bs-border-color);
}

/* Vastusevariandid kahes võrdse laiusega tulbas, kitsal kaardil ühes tulbas */
.answers-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.5rem;
}

@container question-card (max-width: 599.98px) {
  .answers-grid {
    grid-template-columns: 1fr;
  }
}

.section-label {
  font-size: 0.75rem;
  letter-spacing: 0.05em;
}

/* Mullid on sama kujuga nagu staatuse mullid (theme.css .badge-active jt) */
.badge-competence {
  background-color: var(--bs-info-bg-subtle);
  color: var(--bs-info-text-emphasis);
  border: var(--bs-border-width) solid var(--bs-info-border-subtle);
}

.badge-neutral {
  background-color: var(--bs-secondary-bg-subtle);
  color: var(--bs-secondary-text-emphasis);
  border: var(--bs-border-width) solid var(--bs-secondary-border-subtle);
}

.badge-icon {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
}

.caret {
  transition: transform 0.2s ease;
}

.caret.open {
  transform: rotate(180deg);
}

.dropdown-menu-custom {
  --bs-dropdown-min-width: 10rem;
  --bs-dropdown-bg: var(--bs-gray-100);
  --bs-dropdown-border-radius: 0.75rem;
  --bs-dropdown-link-color: var(--bs-black);
}
</style>
