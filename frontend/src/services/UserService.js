import axios from 'axios'

export default {
  getAllUsers() {
    return axios.get('/api/users')
  },
  addUser(newUser) {
    return axios.post('/api/users', newUser)
  },
  updateUserStatus(userId, status) {
    return axios.patch(`/api/users/${userId}/status`, { status })
  },
  deleteUser(userId) {
    return axios.delete(`/api/users/${userId}`)
  },
}
