<script setup lang="ts">
import type { Todo } from '../models/todo'
import TodoItem from './TodoItem.vue'

defineProps<{
  todos: Todo[]
}>()

const emit = defineEmits<{
  toggle: [todo: Todo]
  delete: [id: string]
}>()
</script>

<template>
  <ul class="space-y-3">
    <TodoItem
      v-for="todo in todos"
      :key="todo.id"
      :todo="todo"
      @toggle="emit('toggle', $event)"
      @delete="emit('delete', $event)"
    />
  </ul>
</template>
