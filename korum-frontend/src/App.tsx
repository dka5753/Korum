import {useEffect, useState} from "react";

interface Post {
    id: number;
    title: string;
    content: string;
    author: string;
}

function App() {
    const [posts, setPosts] = useState<Post[]>([]);
    const [message, setMessage] = useState("");
    const [username, setUsername] = useState("");


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
                headers: {
                    Accept: "application/json"
                },
                credentials: "include",
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

    async function handleLogin() {
        const csrf = await getCsrf();

        const response = await fetch("http://localhost:8080/api/login", {
            method: "POST",
            credentials: "include",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
                [csrf.headerName]: csrf.token,
            },
            body: new URLSearchParams({
                username: "kimchi",
                password: "1234",
            }),
        });

        if (response.ok) {
            const meResponse = await fetch("http://localhost:8080/me", {
                credentials: "include",
                headers: {
                    Accept: "application/json",
                },
            });
            if (meResponse.ok) {
                const me = await meResponse.json();
                setUsername(me.username);
                setMessage(`${me.username}님 환영합니다.`);
            } else {
                setMessage("로그인은 성공했지만 사용자 정보를 가져오지 못했습니다.");
            }
        } else {
            setMessage("로그인에 실패했습니다.")
        }
    }

    async function handleLogout() {
        const csrf = await getCsrf();

        const response = await fetch("http://localhost:8080/api/logout", {
            method: "POST",
            credentials: "include",
            headers: {
                [csrf.headerName]: csrf.token,
            },
        });

        if (response.ok) {
            setMessage(`${username}님 로그아웃했습니다.`);
            setUsername("");
        }
    }

    async function handleCreatePost() {
        const csrf = await getCsrf();

        const response = await fetch("http://localhost:8080/posts", {
            method: "POST",
            credentials: "include",
            headers: {
                "Content-Type": "application/json",
                [csrf.headerName]: csrf.token,
            },
            body: JSON.stringify({
                title: "작성자 확인 실습",
                content: "로그인한 사용자가 작성자로 저장되는지 확인",
            }),
        });

        console.log("글쓰기 상태:", response.status);

        if (response.ok) {
            const savedPost: Post = await response.json();

            setPosts((previousPosts) => [savedPost, ...previousPosts]);
        }
    }

    async function handleDeletePost(id: number) {
        const csrf = await getCsrf();

        const response = await fetch(`http://localhost:8080/posts/${id}`, {
            method: "DELETE",
            credentials: "include",
            headers: {
                [csrf.headerName]: csrf.token,
            },
        });

        if (response.ok) {
            setPosts((previousPosts) =>
                previousPosts.filter((post) => post.id !== id)
            );
            setMessage("글을 삭제했습니다.");
        } else if (response.status === 403) {
            setMessage("본인 글만 삭제 가능")
        } else if (response.status === 401) {
            setMessage("로그인 필요");
        } else {
            setMessage(`삭제에 실패했습니다. (${response.status})`);
        }

        console.log("삭제 상태: ", response.status);
    }

    async function handleUpdatePost(id: number) {
        const csrf = await getCsrf();

        const response = await fetch(`http://localhost:8080/posts/${id}`, {
            method: "PUT",
            credentials: "include",
            headers: {
                "Content-Type": "application/json",
                [csrf.headerName]: csrf.token,
            },
            body: JSON.stringify({
                title: "수정한제목",
            }),
        });
        if (response.ok) {
            const updatedPost: Post = await response.json();

            setPosts((previousPost) =>
                previousPost.map((post) =>
                    post.id === id ? updatedPost : post
                )
            );
            setMessage("수정해줄게 까짓거")
        } else if (response.status === 403) {
            setMessage("응 너는 권한없어~ 못해~");
        } else if (response.status === 401) {
            setMessage("로그인부터해 돌아가~");
        } else {
            setMessage(`수정 실패! (${response.status})`);
        }
    }

    return (
        <div>
            <h1>Korum 게시판</h1>
            <p role="status">{message}</p>
            <button onClick={handleGetMe}>내정보 조 회먹고싶다</button>
            <table style={{width: "100%", borderCollapse: "collapse"}}>
                <thead>
                <tr>
                    <th>번호</th>
                    <th>제목</th>
                    <th>작성자</th>
                    <th>관리</th>
                </tr>
                </thead>

                <tbody>
                {posts.map((post) => (
                    <tr key={post.id}>
                        <td>{post.id}</td>
                        <td>
                            <strong>{post.title}</strong>
                            <p>{post.content}</p>
                        </td>
                        <td>{post.author}</td>
                        <td>
                            <button onClick={() => handleDeletePost(post.id)}>
                                삭제
                            </button>
                            <button onClick={() => handleUpdatePost(post.id)}>
                                바꾸고싶어?
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            <button onClick={handleSignup}>회먹고싶은사람 가입</button>
            <button onClick={handleLogin}>회먹을사람 로그인</button>
            <button onClick={handleLogout}>회 다먹었으면 꺼져</button>
            <button onClick={handleCreatePost}>글남기려고?</button>
        </div>
    );
}

export default App;
