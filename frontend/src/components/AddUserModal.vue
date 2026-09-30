<script>
import UserService from '@/services/UserService.js'
import RoleService from '@/services/RoleService.js'
import AlertDanger from '@/components/AlertDanger.vue'
import Status from '@/Status.js'
import { PhEye, PhEyeSlash, PhX } from '@phosphor-icons/vue'

export default {
  name: 'AddUserModal',
  components: { AlertDanger, PhEye, PhEyeSlash, PhX },

  props: {
    isOpen: Boolean,
  },

  emits: ['event-close', 'event-user-added'],

  data() {
    return {
      isSaving: false,
      isPasswordVisible: false,
      errorMessage: '',
      roles: [],
      userStatus: Status,
      statusCodes: ['A', 'P'],
      newUser: {
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        roleId: null,
        status: 'A',
      },
    }
  },

  watch: {
    isOpen(newValue) {
      if (newValue) {
        this.resetForm()
      }
    },
  },

  methods: {
    getAllRoles() {
      RoleService.getAllRoles()
        .then((response) => (this.roles = response.data))
        .catch(() => (this.errorMessage = 'Rollide laadimine ebaõnnestus'))
    },
    addUser() {
      this.isSaving = true
      this.errorMessage = ''
      UserService.addUser(this.newUser)
        .then(() => this.$emit('event-user-added'))
        .catch((error) => this.handleAddUserError(error))
        .finally(() => (this.isSaving = false))
    },
    handleAddUserError(error) {
      this.errorMessage = error.response?.data?.message ?? 'Kasutaja lisamine ebaõnnestus'
    },
    resetForm() {
      this.errorMessage = ''
      this.isPasswordVisible = false
      this.newUser = {
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        roleId: null,
        status: 'A',
      }
    },
  },

  beforeMount() {
    this.getAllRoles()
  },
}
</script>

<template>
  <div v-if="isOpen">
    <div class="modal d-block" tabindex="-1" @click.self="$emit('event-close')">
      <div class="modal-dialog modal-dialog-centered">
        <form class="modal-content rounded-4 border-0 shadow" @submit.prevent="addUser">
          <div class="modal-header border-0 px-4 pt-4">
            <h4 class="modal-title">Lisa kasutaja</h4>
            <button type="button" class="btn btn-sm p-0" @click="$emit('event-close')">
              <PhX :size="22" />
            </button>
          </div>
          <div class="modal-body px-4">
            <AlertDanger :error-message="errorMessage" />
            <div class="row g-3 mb-3">
              <div class="col-sm-6">
                <label for="add-user-first-name" class="form-label fw-semibold">Eesnimi</label>
                <input
                  id="add-user-first-name"
                  v-model="newUser.firstName"
                  type="text"
                  class="form-control"
                  maxlength="255"
                  required
                />
              </div>
              <div class="col-sm-6">
                <label for="add-user-last-name" class="form-label fw-semibold">Perekonnanimi</label>
                <input
                  id="add-user-last-name"
                  v-model="newUser.lastName"
                  type="text"
                  class="form-control"
                  maxlength="255"
                  required
                />
              </div>
            </div>
            <div class="mb-3">
              <label for="add-user-email" class="form-label fw-semibold">E-post</label>
              <input
                id="add-user-email"
                v-model="newUser.email"
                type="email"
                class="form-control"
                placeholder="nimi@example.com"
                required
              />
            </div>
            <div class="mb-3">
              <label for="add-user-password" class="form-label fw-semibold">Parool</label>
              <div class="input-group">
                <input
                  id="add-user-password"
                  v-model="newUser.password"
                  :type="isPasswordVisible ? 'text' : 'password'"
                  class="form-control"
                  minlength="6"
                  maxlength="255"
                  autocomplete="new-password"
                  required
                />
                <button
                  type="button"
                  class="btn btn-outline-secondary d-inline-flex align-items-center"
                  :title="isPasswordVisible ? 'Peida parool' : 'Näita parooli'"
                  @click="isPasswordVisible = !isPasswordVisible"
                >
                  <PhEyeSlash v-if="isPasswordVisible" :size="18" />
                  <PhEye v-else :size="18" />
                </button>
              </div>
              <div class="form-text">Vähemalt 6 märki.</div>
            </div>
            <div class="mb-3">
              <label for="add-user-role" class="form-label fw-semibold">Roll</label>
              <select id="add-user-role" v-model="newUser.roleId" class="form-select" required>
                <option :value="null" disabled>-- Vali roll --</option>
                <option v-for="role in roles" :key="role.roleId" :value="role.roleId">
                  {{ role.roleName }}
                </option>
              </select>
            </div>
            <div class="mb-2">
              <span class="form-label fw-semibold d-block">Staatus</span>
              <div class="d-flex gap-2">
                <label
                  v-for="statusCode in statusCodes"
                  :key="statusCode"
                  class="status-option rounded-pill"
                  :class="{ selected: newUser.status === statusCode }"
                >
                  <input
                    v-model="newUser.status"
                    type="radio"
                    name="add-user-status"
                    class="visually-hidden"
                    :value="statusCode"
                  />
                  <span class="badge rounded-pill" :class="userStatus[statusCode].badgeClass">
                    {{ userStatus[statusCode].name }}
                  </span>
                </label>
              </div>
            </div>
          </div>
          <div class="modal-footer border-0 px-4 pb-4">
            <button type="button" class="btn btn-light rounded-2" @click="$emit('event-close')">
              Tühista
            </button>
            <button type="submit" class="btn btn-color rounded-2" :disabled="isSaving">
              {{ isSaving ? 'Lisan...' : 'Lisa kasutaja' }}
            </button>
          </div>
        </form>
      </div>
    </div>
    <div class="modal-backdrop show"></div>
  </div>
</template>

<style scoped>
.status-option {
  padding: 0.2rem;
  border: 2px solid transparent;
  cursor: pointer;
  opacity: 0.5;
  transition: opacity 0.15s ease;
}

.status-option.selected {
  border-color: var(--bs-blue);
  opacity: 1;
}

.status-option .badge {
  font-size: 0.9rem;
}

.btn-color {
  --bs-btn-bg: var(--bs-gray-200);
  --bs-btn-color: var(--bs-blue);
  font-weight: bold;
  --bs-btn-hover-bg: var(--bs-blue);
  --bs-btn-hover-color: var(--bs-light);
  --bs-btn-active-bg: var(--bs-gray-400);
  --bs-btn-disabled-bg: var(--bs-gray-200);
  --bs-btn-disabled-color: var(--bs-blue);
}
</style>
