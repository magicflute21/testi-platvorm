<script>
import CompetenceService from '@/services/CompetenceService.js'
import Status from '@/Status.js'
import PreviewCard from '@/components/PreviewCard.vue'
import LoadingText from '@/components/LoadingText.vue'
import MainTitle from '@/components/MainTitle.vue'

export default {
  name: 'CompetenceView',
  components: { LoadingText, PreviewCard, MainTitle },

  beforeMount() {
    this.getAllCompetences()
  },

  data() {
    return {
      isLoading: true,
      competenceStatus: Status,
      competences: [
        {
          competenceId: 0,
          competenceName: '',
          shortDescription: '',
          status: '',
        },
      ],
    }
  },
  methods: {
    getAllCompetences() {
      CompetenceService.getAllCompetences()
        .then((response) => this.handleGetAllCompetences(response))
        .catch()
        .finally(() => (this.isLoading = false))
    },
    handleGetAllCompetences(response) {
      this.competences = response.data
    },
    // tühjad meetodid nupuvajutuse näitamiseks
    navigateToCompetenceDetail() {},
    navigateToCompetenceEdit() {},
    changeCompetenceStatus() {},
  },
}
</script>

<template>
  <div class="container py-4">
    <LoadingText v-if="isLoading" />
    <div v-else>
      <MainTitle title="Kompetentsid" />
      <div class="preview-competence-card-grid">
        <PreviewCard
          v-for="competence in competences"
          :key="competence.competenceId"
          :status-badge="competenceStatus[competence.status]"
        >
          <template #title>{{ competence.competenceName }}</template>
          <template #description>{{ competence.shortDescription }}</template>
          <template #menu>
            <li>
              <button
                class="dropdown-item"
                @click="changeCompetenceStatus(competence.competenceId)"
              >
                Muuda staatust
              </button>
            </li>
          </template>
          <template #actions>
            <button
              class="btn btn-primary fw-bold rounded-2"
              @click="navigateToCompetenceDetail(competence.competenceId)"
            >
              Vaata
            </button>
            <button
              class="btn btn-light fw-bold rounded-2"
              @click="navigateToCompetenceEdit(competence.competenceId)"
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
.preview-competence-card-grid {
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
