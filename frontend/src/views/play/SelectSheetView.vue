<script setup>

</script>

<template>
    <div>
        <div>
            <h1 style="color: white;">대기 페이지</h1>
        </div>
        <div id="card-box">
            <div class="card card-compact h-64"> <!-- W-72 -->
                <figure>
                    <img
                    src="https://newsimg.hankookilbo.com/cms/articlerelease/2021/01/17/fb3de445-6b62-49df-9dab-0fa8efa9cc8c.jpg"
                    alt="car!" />
                </figure>
                <div class="card-body">
                    <h2 class="card-title">인기 악보</h2>
                    <p>인기 악보들을 플레이하세요!</p>
                    <div class="card-actions justify-end">
                    </div>
                </div>
            </div>
            <div class="card card-compact h-64">
                <figure>
                    <img
                    src="https://img.hankyung.com/photo/202405/01.36880695.1.jpg"
                    alt="car!" />
                </figure>
                <div class="card-body">
                    <h2 class="card-title">이전 악보</h2>
                    <p>자주 플레이했던 악보를 플레이하세요!</p>
                    <div class="card-actions justify-end">
                    </div>
                </div>
            </div>
            <div class="card card-compact h-64">
                <figure>
                    <img
                    src="https://images.saramingig.co.kr/product/F/0/X/F0Xdpaep3WJVpIR.jpeg/o2j/resize/900"
                    alt="car!" />
                </figure>
                <div class="card-body">
                    <h2 class="card-title">티어 악보</h2>
                    <p>현재 티어와 맞는 악보를 플레이하세요!</p>
                    <div class="card-actions justify-end">
                    </div>
                </div>
            </div>
            <div class="card card-compact h-64">
                <figure>
                    <img
                    src="https://images.saramingig.co.kr/product/i/D/e/iDepWckeH5rM57A.jpeg/o2j/resize/900"
                    alt="car!" />
                </figure>
                <div class="card-body">
                    <h2 class="card-title">랜덤 악보</h2>
                    <p>랜덤한 악보를 플레이하세요!</p>
                    <div class="card-actions justify-end">
                    </div>
                </div>
            </div>
        
        </div>
    </div>
</template>

<style scoped>

#card-box{
    display: flex;
    flex-wrap: nowrap;
    justify-content: space-around;
    margin-top: 20px;
}

.card{
    width: 19vw;
    height: 58vh;
    cursor: pointer;
}

#card-box:has(div:hover) > div:not(:hover){
    filter: blur(4px)
}

#card-box:has(div:hover) > div:hover{
    width: 20vw;
    height: 59vh;
}

div{
    transition: all 150ms ease-in-out;
}


</style>