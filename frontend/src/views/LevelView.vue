<script>
import LevelService from '@/services/LevelService.js'
import PreviewCard from '@/components/PreviewCard.vue'
import LoadingText from '@/components/LoadingText.vue'
import MainTitle from '@/components/MainTitle.vue'

// Iga taseme number saab oma värvi
const LEVEL_STYLES = {
  1: { cardClass: 'level-card-1' },
  2: { cardClass: 'level-card-2' },
  3: { cardClass: 'level-card-3' },
}

// Taseme näidikus on kolm tulpa, millest täidetakse nii mitu, kui kõrge on tase
const LEVEL_BAR_COUNT = 3

export default {
  name: 'LevelView',
  components: { LoadingText, PreviewCard, MainTitle },

  beforeMount() {
    this.getAllLevels()
  },

  data() {
    return {
      isLoading: true,
      levelBarCount: LEVEL_BAR_COUNT,
      levels: [
        {
          levelId: 0,
          level: 0,
          levelName: '',
          description: '',
        },
      ],
    }
  },
  methods: {
    getAllLevels() {
      LevelService.getAllLevels()
        .then((response) => this.handleGetAllLevels(response))
        .catch()
        .finally(() => (this.isLoading = false))
    },
    handleGetAllLevels(response) {
      this.levels = response.data
    },
    // Tasemel pole staatust, seega näitab badge taseme numbrit
    getLevelBadge(level) {
      return { name: 'Tase ' + level.level, badgeClass: 'level-badge' }
    },
    getLevelStyle(level) {
      return LEVEL_STYLES[level.level] ?? LEVEL_STYLES[1]
    },
    // tühjad meetodid nupuvajutuse näitamiseks
    navigateToLevelDetail() {},
    navigateToLevelEdit() {},
  },
}
</script>

<template>
  <div class="container py-4">
    <LoadingText v-if="isLoading" />
    <div v-else>
      <MainTitle title="Tasemed" />
      <div class="preview-level-card-grid">
        <PreviewCard
          v-for="level in levels"
          :key="level.levelId"
          class="level-card"
          :class="getLevelStyle(level).cardClass"
          :status-badge="getLevelBadge(level)"
        >
          <template #title>
            <span class="level-icon" aria-hidden="true">
              <span
                v-for="barNumber in levelBarCount"
                :key="barNumber"
                class="level-bar"
                :class="{ 'level-bar-filled': barNumber <= level.level }"
              />
            </span>
            {{ level.levelName }}
          </template>
          <template #description>{{ level.description }}</template>
          <template #actions>
            <button
              class="btn btn-primary fw-bold rounded-2"
              @click="navigateToLevelDetail(level.levelId)"
            >
              Vaata
            </button>
            <button
              class="btn btn-light fw-bold rounded-2"
              @click="navigateToLevelEdit(level.levelId)"
            >
              Muuda
            </button>
          </template>
        </PreviewCard>
      </div>
    </div>
  </div>
</template>

<style scoped>
.level-card {
  overflow: hidden;
  background: linear-gradient(180deg, var(--level-bg) 0%, var(--bs-body-bg) 55%);
}

/* Värviline riba kaardi ülaservas */
.level-card::before {
  content: '';
  position: absolute;
  inset: 0 0 auto 0;
  height: 6px;
  background-color: var(--level-color);
}

.level-card-1 {
  --level-color: #1f8a4c;
  --level-bg: #e6f4ec;
}

.level-card-2 {
  --level-color: #c25e00;
  --level-bg: #fdf0e3;
}

.level-card-3 {
  --level-color: #1e40af;
  --level-bg: #e8eefc;
}

/* Taseme näidik: täidetud tulbad on täisvärvis, täitmata tulpadel on ainult kontuur */
.level-icon {
  display: inline-flex;
  align-items: flex-end;
  justify-content: center;
  gap: 3px;
  width: 2.5rem;
  height: 2.5rem;
  margin-right: 0.5rem;
  padding-bottom: 0.5rem;
  border-radius: 0.375rem;
  vertical-align: middle;
  background-color: var(--level-color);
}

.level-bar {
  width: 6px;
  border: 2px solid #fff;
  background-color: transparent;
  transition: transform 0.2s ease;
  transform-origin: bottom;
}

.level-bar:nth-child(1) {
  height: 9px;
}

.level-bar:nth-child(2) {
  height: 15px;
}

.level-bar:nth-child(3) {
  height: 21px;
}

.level-bar-filled {
  background-color: #fff;
}

.level-card:hover .level-bar-filled {
  transform: scaleY(1.15);
}

.level-card :deep(.level-badge) {
  color: #fff;
  background-color: var(--level-color);
  letter-spacing: 0.02em;
}

.preview-level-card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(min(450px, 100%), 1fr));
  gap: 1rem;
}

@media (min-width: 1800px) {
  .container {
    max-width: 1760px;
  }
}

@media (min-width: 2400px) {
  .container {
    max-width: 2340px;
  }
}

@media (min-width: 3000px) {
  .container {
    max-width: 2900px;
  }
}
</style>
