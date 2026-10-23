import { useEffect, useState } from "react";

interface Post {
  id: number;
  title: string;
  content: string;
  author: string;
}

function App() {
  const [posts, setPosts] = useState<Post[]>([]);

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

  return (
    <div>
      <h1>Korum 게시판</h1>

      {posts.map((post) => (
        <div key={post.id}>
          <h2>{post.title}</h2>
          <p>{post.content}</p>
          <small>작성자: {post.author}</small>
        </div>
      ))}
    </div>
  );
}

export default App;