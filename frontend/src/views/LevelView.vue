<script>
import LevelService from '@/services/LevelService.js'
import PreviewCard from '@/components/PreviewCard.vue'
import LoadingText from '@/components/LoadingText.vue'
import MainTitle from '@/components/MainTitle.vue'

export default {
  name: 'LevelView',
  components: { LoadingText, PreviewCard, MainTitle },

  beforeMount() {
    this.getAllLevels()
  },

  data() {
    return {
      isLoading: true,
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
    // Tasemel pole staatust, seega näitab badge taseme numbrit oma värviga
    getLevelBadge(level) {
      return { name: 'Tase ' + level.level, badgeClass: 'badge-level-' + level.level }
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
          :status-badge="getLevelBadge(level)"
        >
          <template #title>{{ level.levelName }}</template>
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
/* Taseme märgid samas stiilis nagu staatuse märgid (vt theme.css .badge-active jt) */
.preview-level-card-grid :deep(.badge-level-1) {
  background-color: var(--bs-success-bg-subtle);
  color: var(--bs-success-text-emphasis);
  border: var(--bs-border-width) solid var(--bs-success-border-subtle);
}

.preview-level-card-grid :deep(.badge-level-2) {
  background-color: var(--bs-warning-bg-subtle);
  color: var(--bs-warning-text-emphasis);
  border: var(--bs-border-width) solid var(--bs-warning-border-subtle);
}

.preview-level-card-grid :deep(.badge-level-3) {
  background-color: var(--bs-info-bg-subtle);
  color: var(--bs-info-text-emphasis);
  border: var(--bs-border-width) solid var(--bs-info-border-subtle);
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
