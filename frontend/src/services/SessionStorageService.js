export default {
  userIsLoggedIn() {
    return sessionStorage.getItem('userId') !== null
  },
  // Kas sisselogitud kasutaja roll on lubatud. Ilma rollide nimekirjata on kõik lubatud.
  hasRole(roles) {
    return !roles || roles.includes(sessionStorage.getItem('roleName'))
  },
}
