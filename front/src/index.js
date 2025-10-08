import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import ChosungGame from './App'; // App.js에 코드를 넣었다면

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <ChosungGame />
  </React.StrictMode>
);