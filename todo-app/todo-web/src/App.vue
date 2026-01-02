<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import type { Todo } from './models/todo'
import * as todoService from './services/todoService'
import TodoList from './components/TodoList.vue'

const todos = ref<Todo[]>([])
const newTodoTitle = ref('')
const loading = ref(true)
const submitting = ref(false)
const error = ref<string | null>(null)

const completedCount = computed(() => todos.value.filter(t => t.completed).length)
const totalCount = computed(() => todos.value.length)

onMounted(async () => {
  await loadTodos()
})

async function loadTodos() {
  loading.value = true
  error.value = null
  try {
    todos.value = await todoService.getTodos()
  } catch (e) {
    console.error('Failed to load todos:', e)
    error.value = 'Failed to load todos. Please try again.'
  } finally {
    loading.value = false
  }
}

async function addTodo() {
  const title = newTodoTitle.value.trim()
  if (!title || submitting.value) return

  submitting.value = true
  error.value = null
  try {
    const todo = await todoService.createTodo({ title })
    todos.value.push(todo)
    newTodoTitle.value = ''
  } catch (e) {
    console.error('Failed to create todo:', e)
    error.value = 'Failed to create todo. Please try again.'
  } finally {
    submitting.value = false
  }
}

async function toggleTodo(todo: Todo) {
  error.value = null
  try {
    const updated = await todoService.updateTodo(todo.id, { completed: !todo.completed })
    const index = todos.value.findIndex(t => t.id === todo.id)
    if (index !== -1) {
      todos.value[index] = updated
    }
  } catch (e) {
    console.error('Failed to update todo:', e)
    error.value = 'Failed to update todo. Please try again.'
  }
}

async function deleteTodo(id: string) {
  error.value = null
  try {
    await todoService.deleteTodo(id)
    todos.value = todos.value.filter(t => t.id !== id)
  } catch (e) {
    console.error('Failed to delete todo:', e)
    error.value = 'Failed to delete todo. Please try again.'
  }
}

function dismissError() {
  error.value = null
}
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-orange-400 via-amber-500 to-yellow-500 py-12 px-4">
    <div class="max-w-lg mx-auto">
      <!-- Header -->
      <div class="text-center mb-8">
        <div class="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-white/20 backdrop-blur-sm mb-4">
          <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4"></path>
          </svg>
        </div>
        <h1 class="text-3xl font-bold text-white mb-2">My Tasks</h1>
        <p class="text-white/80">Stay organized, get things done</p>
      </div>

      <!-- Main Card -->
      <div class="bg-white/95 backdrop-blur-xl rounded-3xl shadow-2xl overflow-hidden">
        <!-- Progress Section -->
        <div class="px-6 pt-6 pb-4">
          <div class="flex items-center justify-between mb-2">
            <span class="text-sm font-medium text-gray-600">Progress</span>
            <span class="text-sm font-semibold text-orange-600">
              {{ completedCount }}/{{ totalCount }} completed
            </span>
          </div>
          <div class="h-2 bg-gray-100 rounded-full overflow-hidden">
            <div
              class="h-full bg-gradient-to-r from-orange-500 to-amber-500 rounded-full transition-all duration-500 ease-out"
              :style="{ width: totalCount > 0 ? `${(completedCount / totalCount) * 100}%` : '0%' }"
            ></div>
          </div>
        </div>

        <!-- Error Alert -->
        <div v-if="error" class="mx-6 mb-4 p-4 bg-red-50 border border-red-100 rounded-xl flex items-start gap-3">
          <svg class="w-5 h-5 text-red-500 mt-0.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
          </svg>
          <div class="flex-1">
            <p class="text-sm text-red-700">{{ error }}</p>
          </div>
          <button @click="dismissError" class="text-red-400 hover:text-red-600 transition-colors">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>

        <!-- Add Todo Form -->
        <form @submit.prevent="addTodo" class="px-6 pb-4">
          <div class="flex gap-3">
            <div class="flex-1 relative">
              <input
                type="text"
                v-model="newTodoTitle"
                placeholder="What needs to be done?"
                class="w-full px-4 py-3 bg-gray-50 border-2 border-transparent rounded-xl text-gray-700 placeholder-gray-400 focus:outline-none focus:border-orange-500 focus:bg-white transition-all duration-200"
                :disabled="submitting"
              />
            </div>
            <button
              type="submit"
              :disabled="submitting || !newTodoTitle.trim()"
              class="px-5 py-3 bg-gradient-to-r from-orange-500 to-amber-500 text-white font-medium rounded-xl hover:from-orange-600 hover:to-amber-600 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 flex items-center gap-2 shadow-lg shadow-orange-500/30"
            >
              <svg v-if="submitting" class="animate-spin h-5 w-5" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              <svg v-else class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
              </svg>
              <span class="hidden sm:inline">Add</span>
            </button>
          </div>
        </form>

        <!-- Divider -->
        <div class="h-px bg-gray-100 mx-6"></div>

        <!-- Content Area -->
        <div class="px-6 py-4 min-h-[200px]">
          <!-- Loading State -->
          <div v-if="loading" class="flex flex-col items-center justify-center py-12">
            <div class="relative">
              <div class="w-12 h-12 border-4 border-orange-100 rounded-full"></div>
              <div class="absolute top-0 left-0 w-12 h-12 border-4 border-orange-500 rounded-full border-t-transparent animate-spin"></div>
            </div>
            <p class="mt-4 text-gray-500 text-sm">Loading your tasks...</p>
          </div>

          <!-- Empty State -->
          <div v-else-if="todos.length === 0" class="flex flex-col items-center justify-center py-12">
            <div class="w-24 h-24 bg-gradient-to-br from-orange-100 to-amber-100 rounded-full flex items-center justify-center mb-4">
              <svg class="w-12 h-12 text-orange-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"></path>
              </svg>
            </div>
            <h3 class="text-lg font-semibold text-gray-700 mb-1">No tasks yet</h3>
            <p class="text-gray-400 text-sm text-center">Add your first task above to get started!</p>
          </div>

          <!-- Todo List -->
          <TodoList
            v-else
            :todos="todos"
            @toggle="toggleTodo"
            @delete="deleteTodo"
          />
        </div>

        <!-- Footer -->
        <div class="px-6 py-4 bg-gray-50 border-t border-gray-100">
          <div class="flex items-center justify-between text-sm text-gray-500">
            <span>
              <template v-if="todos.length > 0">
                {{ todos.length - completedCount }} tasks remaining
              </template>
              <template v-else>
                Ready to be productive?
              </template>
            </span>
            <div class="flex items-center gap-1">
              <span class="text-orange-500">Powered by</span>
              <span class="font-semibold text-orange-600">Java + Vue</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Attribution -->
      <p class="text-center text-white/60 text-sm mt-6">
        Built with k8s-ephemeral-environments
      </p>
    </div>
  </div>
</template>
