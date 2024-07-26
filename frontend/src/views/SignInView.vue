<template>

    <div id="signin-box">

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
                <button @click="cl" type="submit">next</button>
            </form>
            
            <div @click="cl">
                클릭
            </div>
        </div>

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

const cl = function() {
    console.log("click");
}


const signIn = function () {

    console.log("signin 메서드 실행")
    
    formData.append("email", email.value);
    formData.append("pwdHash", pwdHash.value);
    formData.append("birthDate", birthDate.value);
    formData.append("nickname", nickname.value);
    formData.append("gender", gender.value);

    const url = "http://localhost:8080/users"

    axios.post(url, formData, {
        headers: {
            'Content-Type': 'application/json; charset=utf-8'
        }
    })
    .catch((error) => {
        console.log("회원가입 실패")
        alert("회원가입에 실패했습니다.")
    })
    .then((response) => {
        if (response.status == 200) {
            console.log(response)

        }
    })




}

</script>

<style>

#signin-box{
    display: flex;
    justify-content: center;
    flex-direction: column;
    border: 1px solid red;
    height: 100%;
    align-items: center;
}

</style>