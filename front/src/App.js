import React, { useState } from 'react';
import { Users, Trophy, Clock, CheckCircle, XCircle } from 'lucide-react';

export default function ChosungGame() {
  const [gameState, setGameState] = useState('waiting'); // waiting, playing, result
  const [chosung, setChosung] = useState('ㄱㄴ');
  const [userAnswer, setUserAnswer] = useState('');
  const [timeLeft, setTimeLeft] = useState(30);
  
  const [players] = useState([
    { id: 1, name: '플레이어1', answer: '과녁', status: 'correct', score: 120 },
    { id: 2, name: '플레이어2', answer: '과느', status: 'wrong', score: 80 },
    { id: 3, name: '플레이어3', answer: '기념', status: 'correct', score: 100 },
    { id: 4, name: '나', answer: '', status: 'playing', score: 95 },
  ]);

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-600 via-pink-500 to-orange-400 p-4">
      <div className="max-w-4xl mx-auto">
        {/* Header */}
        <div className="bg-white/95 backdrop-blur rounded-3xl shadow-2xl p-6 mb-6">
          <div className="flex justify-between items-center">
            <div className="flex items-center gap-3">
              <Trophy className="w-8 h-8 text-yellow-500" />
              <h1 className="text-3xl font-bold bg-gradient-to-r from-purple-600 to-pink-600 bg-clip-text text-transparent">
                초성게임
              </h1>
            </div>
            <div className="flex items-center gap-4">
              <div className="flex items-center gap-2 bg-purple-100 px-4 py-2 rounded-full">
                <Users className="w-5 h-5 text-purple-600" />
                <span className="font-semibold text-purple-600">{players.length}명</span>
              </div>
              <div className="flex items-center gap-2 bg-orange-100 px-4 py-2 rounded-full">
                <Clock className="w-5 h-5 text-orange-600" />
                <span className="font-semibold text-orange-600 text-xl">{timeLeft}초</span>
              </div>
            </div>
          </div>
        </div>

        {/* Main Game Area */}
        <div className="bg-white/95 backdrop-blur rounded-3xl shadow-2xl p-8 mb-6">
          {/* Chosung Display */}
          <div className="text-center mb-8">
            <p className="text-gray-600 mb-3 text-lg">주어진 초성</p>
            <div className="bg-gradient-to-r from-purple-500 to-pink-500 rounded-2xl p-8 mb-6">
              <div className="text-8xl font-bold text-white tracking-widest">
                {chosung}
              </div>
            </div>
            <p className="text-gray-500 text-sm">이 초성으로 시작하는 단어를 입력하세요!</p>
          </div>

          {/* Answer Input */}
          <div className="max-w-md mx-auto">
            <input
              type="text"
              value={userAnswer}
              onChange={(e) => setUserAnswer(e.target.value)}
              placeholder="단어를 입력하세요..."
              className="w-full px-6 py-4 text-2xl text-center border-4 border-purple-300 rounded-2xl focus:border-purple-500 focus:outline-none transition-all"
            />
            <button className="w-full mt-4 bg-gradient-to-r from-purple-600 to-pink-600 text-white text-xl font-bold py-4 rounded-2xl hover:shadow-lg hover:scale-105 transition-all">
              제출하기
            </button>
          </div>
        </div>

        {/* Players Status */}
        <div className="bg-white/95 backdrop-blur rounded-3xl shadow-2xl p-6">
          <h2 className="text-xl font-bold text-gray-800 mb-4 flex items-center gap-2">
            <Users className="w-6 h-6" />
            참가자 현황
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {players.map((player) => (
              <div
                key={player.id}
                className={`p-4 rounded-xl border-2 transition-all ${
                  player.status === 'correct'
                    ? 'bg-green-50 border-green-300'
                    : player.status === 'wrong'
                    ? 'bg-red-50 border-red-300'
                    : 'bg-blue-50 border-blue-300'
                }`}
              >
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <div className={`w-10 h-10 rounded-full flex items-center justify-center font-bold text-white ${
                      player.status === 'correct'
                        ? 'bg-green-500'
                        : player.status === 'wrong'
                        ? 'bg-red-500'
                        : 'bg-blue-500'
                    }`}>
                      {player.name[0]}
                    </div>
                    <div>
                      <p className="font-semibold text-gray-800">{player.name}</p>
                      <p className="text-sm text-gray-500">점수: {player.score}</p>
                    </div>
                  </div>
                  {player.status === 'correct' && (
                    <div className="flex items-center gap-2">
                      <span className="text-green-700 font-semibold">{player.answer}</span>
                      <CheckCircle className="w-6 h-6 text-green-500" />
                    </div>
                  )}
                  {player.status === 'wrong' && (
                    <div className="flex items-center gap-2">
                      <span className="text-red-700 font-semibold line-through">{player.answer}</span>
                      <XCircle className="w-6 h-6 text-red-500" />
                    </div>
                  )}
                  {player.status === 'playing' && (
                    <span className="text-blue-600 font-semibold animate-pulse">입력중...</span>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}