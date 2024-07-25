import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useThemeStore = defineStore('theme', () => {
  const isDarkMode = ref(true);
  document.querySelector("html").setAttribute('data-theme', "dark")
  const switchTheme = () => {
    if (isDarkMode.value) {
      isDarkMode.value = false;
      document.querySelector("html").setAttribute('data-theme', "light")
    } else {
      isDarkMode.value = true;
      document.querySelector("html").setAttribute('data-theme', "dark")
    }
  }

  // const changeTheme = (themeAfter) => {
  //   theme.value = themeAfter;
  //   document.querySelector("html").setAttribute('data-theme', theme.value)
  // }

  return {
    isDarkMode,
    switchTheme,
    // changeTheme,
  };
})
