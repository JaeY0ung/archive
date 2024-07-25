<template>
    <form @submit.prevent="signIn">
                <div id="form-id-box">
                    <div id="id-box-text">email</div>
                    <input id="id-box-input" name="email" v-model="email" />
                </div>
                <div id="form-pw-box">
                    <div id="pw-box-text">pwdHash</div>
                    <input id="pw-box-input" name="pwdHash" v-model="pwdHash" />
                </div>
                <div>
                    <div>birthDate</div>
                    <input name="birthDate" v-model="birthDate" />
                </div>
                <div>
                    <div>nickname</div>
                    <input name="nickname" v-model="nickname" />
                </div>
                <div>
                    <div>gender</div>
                    <input name="gender" v-model="gender" />
                </div>
                <button id="form-next-button">next</button>
            </form>
</template>

<script setup>
import {ref} from 'vue';
import axios from 'axios';

const formData = new FormData();

const email = ref('');
const pwdHash = ref('');
const birthDate = ref('');
const nickname = ref('');
const gender = ref('');



const signIn = function () {
    
    formData.append("email", email.value);
    formData.append("pwdHash", pwdHash.value);
    formData.append("birthDate", birthDate.value);
    formData.append("nickname", nickname.value);
    formData.append("gender", gender.value);

    const url = "http://localhost:8080/users"

    axios.post(url, formData)
    .catch((error) => {
        console.log("회원가입 실패")
        alert("회원가입에 실패했습니다.")
    })
    .then((response) => {
        if (response.status == 200) {
            console.log(response)
            store.login(id.value,pw.value)

        }
    })




}

</script>

<style>

</style>