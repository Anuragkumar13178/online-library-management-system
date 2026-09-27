import axios from 'axios'
export const api = axios.create({ baseURL: import.meta.env.VITE_API_URL || '/api' })
api.interceptors.request.use(config => { const token = sessionStorage.getItem('library-token'); if (token) config.headers.Authorization = `Bearer ${token}`; return config })
api.interceptors.response.use(r => r, error => { if (error.response?.status === 401) { sessionStorage.removeItem('library-token'); sessionStorage.removeItem('library-user'); window.location.assign('/login') } return Promise.reject(error) })
export type User = { id:number; name:string; email:string; role:'LIBRARIAN'|'MEMBER'; phone?:string; accountStatus:string; createdAt?:string }
export type Book = { id:number; title:string; author:string; isbn:string; genre:string; publisher:string; publicationYear:number; quantity:number; availableCopies:number; coverImage:string; description:string }
export type Loan = { id:number; book:Book; member:{id:number;name:string;email:string}; borrowDate:string;dueDate:string;returnDate:string;status:'BORROWED'|'RETURNED'|'OVERDUE' }
