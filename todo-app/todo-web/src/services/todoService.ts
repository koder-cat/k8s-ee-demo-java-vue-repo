import type { Todo, CreateTodoDto, UpdateTodoDto } from '../models/todo'

const API_URL = '/api/todos'

export async function getTodos(): Promise<Todo[]> {
  const response = await fetch(API_URL)
  if (!response.ok) {
    throw new Error('Failed to fetch todos')
  }
  return response.json()
}

export async function getTodoById(id: string): Promise<Todo> {
  const response = await fetch(`${API_URL}/${id}`)
  if (!response.ok) {
    throw new Error('Failed to fetch todo')
  }
  return response.json()
}

export async function createTodo(dto: CreateTodoDto): Promise<Todo> {
  const response = await fetch(API_URL, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(dto),
  })
  if (!response.ok) {
    throw new Error('Failed to create todo')
  }
  return response.json()
}

export async function updateTodo(id: string, dto: UpdateTodoDto): Promise<Todo> {
  const response = await fetch(`${API_URL}/${id}`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(dto),
  })
  if (!response.ok) {
    throw new Error('Failed to update todo')
  }
  return response.json()
}

export async function deleteTodo(id: string): Promise<void> {
  const response = await fetch(`${API_URL}/${id}`, {
    method: 'DELETE',
  })
  if (!response.ok) {
    throw new Error('Failed to delete todo')
  }
}
