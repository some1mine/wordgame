import { fireEvent, render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import App from './App';

test('renders login form', () => {
  render(<App />);

  expect(screen.getByRole('heading', { name: '초성게임' })).toBeInTheDocument();
  expect(screen.getByPlaceholderText('아이디')).toBeInTheDocument();
  expect(screen.getByPlaceholderText('비밀번호')).toBeInTheDocument();
  expect(screen.getByRole('button', { name: '로그인' })).toBeInTheDocument();
});

test('moves between login and registration screens', () => {
  render(<App />);

  fireEvent.click(screen.getByRole('button', { name: '계정이 없으신가요? 회원가입하기' }));
  expect(screen.getByRole('heading', { name: '회원가입' })).toBeInTheDocument();

  fireEvent.click(screen.getByRole('button', { name: '로그인으로 돌아가기' }));
  expect(screen.getByRole('button', { name: '로그인' })).toBeInTheDocument();
});
