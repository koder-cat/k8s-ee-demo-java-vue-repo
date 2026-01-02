<script setup lang="ts">
import type { Todo } from '../models/todo'

defineProps<{
  todo: Todo
}>()

const emit = defineEmits<{
  toggle: [todo: Todo]
  delete: [id: string]
}>()
</script>

<template>
  <li
    class="group flex items-center gap-4 p-4 rounded-xl transition-all duration-200"
    :class="[
      todo.completed ? 'bg-green-50 hover:bg-green-100' : 'bg-gray-50 hover:bg-gray-100'
    ]"
  >
    <!-- Custom Checkbox -->
    <button
      @click="emit('toggle', todo)"
      class="relative flex-shrink-0 w-6 h-6 rounded-full border-2 transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-orange-500"
      :class="[
        todo.completed ? 'border-green-500 bg-green-500' : 'border-gray-300'
      ]"
    >
      <svg
        v-if="todo.completed"
        class="absolute inset-0 w-full h-full text-white p-1"
        fill="none"
        stroke="currentColor"
        viewBox="0 0 24 24"
      >
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M5 13l4 4L19 7"></path>
      </svg>
    </button>

    <!-- Task Text -->
    <span
      class="flex-1 transition-all duration-200"
      :class="[
        todo.completed ? 'line-through text-gray-400' : 'text-gray-700'
      ]"
    >
      {{ todo.title }}
    </span>

    <!-- Delete Button -->
    <button
      @click="emit('delete', todo.id)"
      class="opacity-0 group-hover:opacity-100 p-2 text-gray-400 hover:text-red-500 hover:bg-red-50 rounded-lg transition-all duration-200 focus:outline-none focus:opacity-100"
      title="Delete task"
    >
      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
      </svg>
    </button>
  </li>
</template>
