import axios from "axios";

const { VUE_APP_REQUEST_URL } = process.env;


function localAxios() {
  const instance = axios.create({
    baseURL: VUE_APP_REQUEST_URL,
    withCredentials: true,
  });
  instance.defaults.headers.common["Authorization"] = "";
  instance.defaults.headers.post["Content-Type"] = "application/json";
  instance.defaults.headers.put["Content-Type"] = "application/json";

  return instance;
}

export { localAxios };
