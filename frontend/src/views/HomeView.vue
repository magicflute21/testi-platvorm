<script>
import TestComponent from '@/components/TestComponent.vue'
import TableTable from "@/components/Table.vue";

export default {
  name: 'HomeView',
  components: {TableTable, TestComponent },
}
</script>

<template>
  <div class="container text-center">
<!--    <div class="alert alert-primary" role="alert">See on koduvaade, see asub rajal (/)</div>-->
<!--    <TestComponent />-->
    <div class="mt-8 mx-auto">
      <TableTable />
    </div>
  </div>
</template>
