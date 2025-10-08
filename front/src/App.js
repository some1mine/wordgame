import React, { useState, useEffect } from 'react';
import { Users, Trophy, Clock, CheckCircle, XCircle, LogIn, UserPlus, Plus, DoorOpen } from 'lucide-react';

// API 기본 URL 설정
const API_BASE_URL = 'http://localhost:8080'; // 여기를 실제 서버 주소로 변경하세요

export default function ChosungGame() {
  const [gameState, setGameState] = useState('login'); // login, lobby, playing, result
  const [chosung, setChosung] = useState('ㄱㄴ');
  const [userAnswer, setUserAnswer] = useState('');
  const [timeLeft, setTimeLeft] = useState(30);
  const [userId, setUserId] = useState(null);
  const [gameId, setGameId] = useState(null);
  
  // 로그인/회원가입 폼 상태
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  
  const [players, setPlayers] = useState([
    { id: 1, name: '플레이어1', answer: '과녁', status: 'correct', score: 120 },
    { id: 2, name: '플레이어2', answer: '과느', status: 'wrong', score: 80 },
    { id: 3, name: '플레이어3', answer: '기념', status: 'correct', score: 100 },
    { id: 4, name: '나', answer: '', status: 'playing', score: 95 },
  ]);

  const [allGames, setAllGames] = useState([]);

  // ============ API 함수들 ============

  // 1. 모든 게임 목록 가져오기
  const fetchAllGames = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/game/all`);
      if (!response.ok) throw new Error('네트워크 응답 실패');
      const data = await response.json();
      console.log('모든 게임:', data);
      setAllGames(data);
    } catch (error) {
      console.error('게임 목록 가져오기 실패:', error);
      alert('게임 목록을 불러오는데 실패했습니다');
    }
  };

  // 2. 특정 게임 정보 가져오기
  const fetchGame = async (gameId) => {
    try {
      const response = await fetch(`${API_BASE_URL}/game/get?gameId=${gameId}`);
      if (!response.ok) throw new Error('네트워크 응답 실패');
      const data = await response.json();
      console.log('게임 정보:', data);
      return data;
    } catch (error) {
      console.error('게임 정보 가져오기 실패:', error);
      alert('게임 정보를 불러오는데 실패했습니다');
    }
  };

  // 3. 게임 만들기
  const makeGame = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/game/make-game`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          userId: userId,
          // 필요한 다른 파라미터들 추가
        }),
      });
      if (!response.ok) throw new Error('네트워크 응답 실패');
      const data = await response.json();
      console.log('게임 생성 완료:', data);
      setGameId(data.gameId);
      setGameState('playing');
      alert('게임이 생성되었습니다!');
    } catch (error) {
      console.error('게임 생성 실패:', error);
      alert('게임 생성에 실패했습니다');
    }
  };

  // 4. 게임 참가하기
  const joinGame = async (selectedGameId) => {
    try {
      const response = await fetch(`${API_BASE_URL}/game/join-game`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          userId: userId,
          gameId: selectedGameId,
        }),
      });
      if (!response.ok) throw new Error('네트워크 응답 실패');
      const data = await response.json();
      console.log('게임 참가 완료:', data);
      setGameId(selectedGameId);
      setGameState('playing');
      alert('게임에 참가했습니다!');
    } catch (error) {
      console.error('게임 참가 실패:', error);
      alert('게임 참가에 실패했습니다');
    }
  };

  // 5. 답안 제출하기
  const submitAnswer = async () => {
    if (!userAnswer.trim()) {
      alert('답을 입력해주세요!');
      return;
    }
    try {
      const response = await fetch(`${API_BASE_URL}/game/submit`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          userId: userId,
          gameId: gameId,
          answer: userAnswer,
        }),
      });
      if (!response.ok) throw new Error('네트워크 응답 실패');
      const data = await response.json();
      console.log('답안 제출 완료:', data);
      setUserAnswer('');
      alert('답안이 제출되었습니다!');
    } catch (error) {
      console.error('답안 제출 실패:', error);
      alert('답안 제출에 실패했습니다');
    }
  };

  // 6. 게임 나가기
  const exitGame = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/game/exit-game`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          userId: userId,
          gameId: gameId,
        }),
      });
      if (!response.ok) throw new Error('네트워크 응답 실패');
      const data = await response.json();
      console.log('게임 나가기 완료:', data);
      setGameState('lobby');
      setGameId(null);
      alert('게임에서 나갔습니다');
    } catch (error) {
      console.error('게임 나가기 실패:', error);
      alert('게임 나가기에 실패했습니다');
    }
  };

  // 7. 게임 종료 확인
  const endGameIfNeed = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/game/end-if-need`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          gameId: gameId,
        }),
      });
      if (!response.ok) throw new Error('네트워크 응답 실패');
      const data = await response.json();
      console.log('게임 종료 확인:', data);
      if (data.isEnded) {
        setGameState('result');
        alert('게임이 종료되었습니다!');
      }
    } catch (error) {
      console.error('게임 종료 확인 실패:', error);
    }
  };

  // 8. 회원가입
  const handleJoin = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/user/join`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          username: username,
          password: password,
        }),
      });
      if (!response.ok) throw new Error('네트워크 응답 실패');
      const data = await response.json();
      console.log('회원가입 완료:', data);
      alert('회원가입이 완료되었습니다!');
    } catch (error) {
      console.error('회원가입 실패:', error);
      alert('회원가입에 실패했습니다');
    }
  };

  // 9. 로그인
  const handleLogin = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/user/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          username: username,
          password: password,
        }),
      });
      if (!response.ok) throw new Error('네트워크 응답 실패');
      const data = await response.json();
      console.log('로그인 완료:', data);
      setUserId(data.userId);
      setGameState('lobby');
      alert('로그인 성공!');
    } catch (error) {
      console.error('로그인 실패:', error);
      alert('로그인에 실패했습니다');
    }
  };

  // ============ useEffect ============

  // 주기적으로 게임 상태 체크 (게임 중일 때만)
  useEffect(() => {
    if (gameState === 'playing' && gameId) {
      const interval = setInterval(() => {
        endGameIfNeed();
        fetchGame(gameId);
      }, 3000); // 3초마다 체크

      return () => clearInterval(interval);
    }
  }, [gameState, gameId]);

  // 로비에 있을 때 게임 목록 가져오기
  useEffect(() => {
    if (gameState === 'lobby') {
      fetchAllGames();
    }
  }, [gameState]);

  // ============ 렌더링 ============

  // 로그인 화면
  if (gameState === 'login') {
    return (
      <div className="min-h-screen bg-gradient-to-br from-purple-600 via-pink-500 to-orange-400 flex items-center justify-center p-4">
        <div className="bg-white/95 backdrop-blur rounded-3xl shadow-2xl p-8 w-full max-w-md">
          <h1 className="text-4xl font-bold text-center mb-8 bg-gradient-to-r from-purple-600 to-pink-600 bg-clip-text text-transparent">
            초성게임
          </h1>
          <div className="space-y-4">
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="아이디"
              className="w-full px-4 py-3 border-2 border-purple-300 rounded-xl focus:border-purple-500 focus:outline-none"
            />
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="비밀번호"
              className="w-full px-4 py-3 border-2 border-purple-300 rounded-xl focus:border-purple-500 focus:outline-none"
            />
            <button
              onClick={handleLogin}
              className="w-full bg-gradient-to-r from-purple-600 to-pink-600 text-white font-bold py-3 rounded-xl hover:shadow-lg hover:scale-105 transition-all flex items-center justify-center gap-2"
            >
              <LogIn className="w-5 h-5" />
              로그인
            </button>
            <button
              onClick={handleJoin}
              className="w-full bg-white text-purple-600 border-2 border-purple-600 font-bold py-3 rounded-xl hover:bg-purple-50 transition-all flex items-center justify-center gap-2"
            >
              <UserPlus className="w-5 h-5" />
              회원가입
            </button>
          </div>
        </div>
      </div>
    );
  }

  // 로비 화면
  if (gameState === 'lobby') {
    return (
      <div className="min-h-screen bg-gradient-to-br from-purple-600 via-pink-500 to-orange-400 p-4">
        <div className="max-w-4xl mx-auto">
          <div className="bg-white/95 backdrop-blur rounded-3xl shadow-2xl p-6 mb-6">
            <div className="flex justify-between items-center">
              <h1 className="text-3xl font-bold bg-gradient-to-r from-purple-600 to-pink-600 bg-clip-text text-transparent">
                게임 로비
              </h1>
              <button
                onClick={makeGame}
                className="bg-gradient-to-r from-purple-600 to-pink-600 text-white font-bold px-6 py-3 rounded-xl hover:shadow-lg hover:scale-105 transition-all flex items-center gap-2"
              >
                <Plus className="w-5 h-5" />
                새 게임 만들기
              </button>
            </div>
          </div>

          <div className="bg-white/95 backdrop-blur rounded-3xl shadow-2xl p-6">
            <h2 className="text-xl font-bold text-gray-800 mb-4">참가 가능한 게임</h2>
            <div className="space-y-3">
              {allGames.length === 0 ? (
                <p className="text-gray-500 text-center py-8">참가 가능한 게임이 없습니다</p>
              ) : (
                allGames.map((game) => (
                  <div
                    key={game.id}
                    className="p-4 border-2 border-purple-300 rounded-xl hover:bg-purple-50 transition-all flex justify-between items-center"
                  >
                    <div>
                      <p className="font-semibold text-gray-800">게임 #{game.id}</p>
                      <p className="text-sm text-gray-500">참가자: {game.playerCount}명</p>
                    </div>
                    <button
                      onClick={() => joinGame(game.id)}
                      className="bg-purple-600 text-white font-bold px-6 py-2 rounded-lg hover:bg-purple-700 transition-all"
                    >
                      참가하기
                    </button>
                  </div>
                ))
              )}
            </div>
          </div>
        </div>
      </div>
    );
  }

  // 게임 화면
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
              <button
                onClick={exitGame}
                className="bg-red-500 text-white font-bold px-4 py-2 rounded-full hover:bg-red-600 transition-all flex items-center gap-2"
              >
                <DoorOpen className="w-5 h-5" />
                나가기
              </button>
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
              onKeyPress={(e) => e.key === 'Enter' && submitAnswer()}
              placeholder="단어를 입력하세요..."
              className="w-full px-6 py-4 text-2xl text-center border-4 border-purple-300 rounded-2xl focus:border-purple-500 focus:outline-none transition-all"
            />
            <button 
              onClick={submitAnswer}
              className="w-full mt-4 bg-gradient-to-r from-purple-600 to-pink-600 text-white text-xl font-bold py-4 rounded-2xl hover:shadow-lg hover:scale-105 transition-all">
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