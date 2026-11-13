import { Link, useNavigate } from "react-router"
import { useState } from "react"

export default function SignupPage() {
  const [email, setEmail] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const navigate = useNavigate();

  async function getCsrf() {
    const response = await fetch("http://localhost:8080/csrf", {
      credentials: "include",
    });
    if (!response.ok) {
      throw new Error('요청 실패');
    }
    return response.json();
  }

  async function handleSignup() {
    const csrf = await getCsrf();
    const response = await fetch(
      "http://localhost:8080/signup", {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
        [csrf.headerName]: csrf.token,
      },
      body: JSON.stringify({
        email: email,
        username: username,
        password: password,
      }),
    }
    );

    if (response.ok) {
      alert("회원가입이 완료되었습니다.")
      navigate("/");
    } else {
      const data = await response.json();

      setMessage(data.error);
    }
  }

  return (
    <div>
      <h1>회원가입</h1>
      <input
        type="email"
        placeholder="이메일"
        value={email}
        onChange={(event) => setEmail(event.target.value)}
      />
      <input
        type="text"
        placeholder="아이디"
        value={username}
        onChange={(event) => setUsername(event.target.value)}
      />
      <input
        type="password"
        placeholder="비밀번호"
        value={password}
        onChange={(event) => setPassword(event.target.value)}
      />
      <p>{message}</p>
      <button onClick={handleSignup}>회원가입</button>
      <Link to="/"> 게시판으로 </Link>
    </div>
  );
}
