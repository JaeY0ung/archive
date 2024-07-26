import axios from "axios";

const { VITE_REQUEST_URL } = import.meta.env;

function localAxios() {
  const instance = axios.create({
    baseURL: VITE_REQUEST_URL,
    withCredentials: true,  // 모든 요청에 대해 withCredentials를 true로 설정
  });
  instance.defaults.headers.common["Authorization"] = "";
  instance.defaults.headers.post["Content-Type"] = "application/json";
  instance.defaults.headers.put["Content-Type"] = "application/json";

  return instance;
}

export { localAxios };
