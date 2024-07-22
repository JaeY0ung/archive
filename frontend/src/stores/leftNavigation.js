import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useLeftNavigationStore = defineStore('leftNavigation', () => {
  
  const navVisibility = ref(false);

  const changeNavVisibility = () => {
    navVisibility.value = !navVisibility.value;
  }
  
  const openNav = () => {
    if (navVisibility.value) navVisibility.value = true;
  }

  const closeNav = () => {
    if (navVisibility.value) navVisibility.value = false;
  }

  return {
    navVisibility,
    changeNavVisibility,
    openNav,
    closeNav,
  };
})
