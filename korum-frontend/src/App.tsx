import { useEffect, useState } from "react";

interface Post {
  id: number;
  title: string;
  content: string;
  author: string;
}

function App() {
  const [posts, setPosts] = useState<Post[]>([]);
  const headers = {
    accept: "application/json",
    Authorization: "Basic " + btoa("user:1234")
  }

  useEffect(() => {
    fetch("http://localhost:8080/posts")
      .then((response) => {
        if (!response.ok) {
          throw new Error("게시글 조회 실패");
        }
        return response.json();
      })
      .then((data: Post[]) => setPosts(data))
      .catch(console.error);
  }, []);
  async function handleGetMe() {
    const response = await fetch("http://localhost:8080/me",
      {
        headers: headers,
        credentials: "omit",
      });

    if (!response.ok) {
      throw new Error(`요청실패: ${response.status}`);
    }
    const data = await response.json();
    console.log(data);
  }
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
                  email: "kim12@example.test",
                  username: "kimchi",
                  password: "1234",
              }),
          }
      );
      console.log("회원가입 상태:", response.status);
  }


  return (
    <div>
      <h1>Korum 게시판</h1>
      <button onClick={handleGetMe}>내정보 조 회먹고싶다</button>
      {posts.map((post) => (
        <div key={post.id}>
          <h2>{post.title}</h2>
          <p>{post.content}</p>
          <small>작성자: {post.author}</small>
        </div>
      ))}
        <button onClick={handleSignup}>회먹고싶은사람 가입</button>
    </div>
  );
}

export default App;
