<script>
import { PhPlus } from '@phosphor-icons/vue'

export default {
  name: 'TestCreateView',
  components: { PhPlus },
}
</script>

<template>
  <div>
    <div class="container text-start col-6 mt-5 mb-5">
      <h1>Loo uus test</h1>
      <p class="mb-5">Täida kõik väljad ja loo uus test.</p>

      <form>
        <div class="row mb-3">
          <label for="inputTestHeading" class="col-sm-3 col-form-label">Pealkiri</label>
          <div class="col-sm-9">
            <input
              type="text"
              class="form-control"
              id="inputTestHeading"
              placeholder="Sisesta testi pealkiri"
            />
          </div>
        </div>
        <div class="row mb-3">
          <label for="inputTestShortDescription" class="col-sm-3 col-form-label"
            >Lühikirjeldus</label
          >
          <div class="col-sm-9">
            <input
              type="text"
              class="form-control"
              id="inputTestShortDescription"
              placeholder="Lisa testi lühikirjeldus"
            />
          </div>
        </div>
        <div class="row mb-3">
          <label for="inputTestDescription" class="col-sm-3 col-form-label">Kirjeldus</label>
          <div class="col-sm-9">
            <textarea
              class="form-control"
              id="inputTestDescription"
              rows="4"
              placeholder="Lisa testi kirjeldus"
            ></textarea>
          </div>
        </div>

        <div class="row mb-3">
          <label for="selectTestCompetence" class="col-sm-3 col-form-label">Kompetents</label>
          <div class="col-sm-5">
            <select id="selectTestCompetence" class="form-select">
              <option selected>Vali kompetents...</option>
              <option>...</option>
              <option>...</option>
              <option>...</option>
            </select>
          </div>
        </div>

        <div class="row mb-3">
          <label for="selectTestLevel" class="col-sm-3 col-form-label">Tase</label>
          <div class="col-sm-5">
            <select id="selectTestLevel" class="form-select">
              <option selected>Vali tase...</option>
              <option>...</option>
              <option>...</option>
              <option>...</option>
            </select>
          </div>
        </div>

        <div class="row mb-3">
          <label for="selectTestPassPercent" class="col-sm-3 col-form-label">Läbimise %</label>
          <div class="col-sm-5">
            <select id="selectTestPassPercent" class="form-select">
              <option selected>Vali läbimise %</option>
              <option>50%</option>
              <option>60%</option>
              <option>70%</option>
              <option>80%</option>
              <option>90%</option>
            </select>
          </div>
        </div>

        <fieldset class="row mb-3">
          <legend class="col-form-label col-sm-3 pt-0 text-nowrap">Punktide ümardamine</legend>
          <div class="col-sm-9">
            <div class="form-check form-check-inline">
              <input
                class="form-check-input"
                type="radio"
                name="roundScore"
                id="radioRoundScoreUp"
                value="true"
              />
              <label class="form-check-label" for="radioRoundScoreUp">Üles</label>
            </div>
            <div class="form-check form-check-inline">
              <input
                class="form-check-input"
                type="radio"
                name="roundScore"
                id="radioRoundScoreDown"
                value="false"
              />
              <label class="form-check-label" for="radioRoundScoreDown">Alla</label>
            </div>
          </div>
        </fieldset>

        <div class="row mb-3">
          <label for="selectTestTimer" class="col-sm-3 col-form-label">Taimer</label>
          <div class="col-sm-5">
            <select id="selectTestTimer" class="form-select">
              <option selected>Ilma taimerita</option>
              <option>15 min</option>
              <option>30 min</option>
              <option>45 min</option>
              <option>60 min</option>
            </select>
          </div>
        </div>

        <div class="row mb-3">
          <label for="selectTestQuestion" class="col-sm-3 col-form-label">Küsimused</label>
          <div class="col-sm-9 d-flex align-items-center gap-2">
            <select id="selectTestQuestion" class="form-select">
              <option selected>Vali küsimus...</option>
              <option>...</option>
              <option>...</option>
              <option>...</option>
            </select>
            <button
              type="button"
              class="btn btn-outline-secondary rounded-circle btn-circle"
              title="Lisa uus küsimus"
            >
              <PhPlus :size="16" />
            </button>
          </div>
        </div>

        <div class="d-flex justify-content-center gap-3 mt-4">
          <button type="button" class="btn btn-link text-body text-decoration-none">Tühista</button>
          <button type="submit" class="btn btn-info text-white">Loo test</button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.btn-circle {
  width: 38px;
  height: 38px;
  flex-shrink: 0;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
