package com.Zihmaho6.Tekloc.presentation.app

import androidx.lifecycle.ViewModel
import com.Zihmaho6.Tekloc.core.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val backStack = ArrayList<Screen>()

    fun openMenu() {
        backStack.clear()
        _screen.value = Screen.Menu
    }

    fun navigate(target: Screen) {
        val current = _screen.value
        if (current !is Screen.Splash) backStack.add(current)
        _screen.value = target
    }

    fun replaceTop(target: Screen) {
        _screen.value = target
    }

    fun replaceGameWith(target: Screen) {
        while (backStack.isNotEmpty() && backStack.last() is Screen.Game) {
            backStack.removeAt(backStack.size - 1)
        }
        _screen.value = target
    }

    fun backToMenu() {
        backStack.clear()
        _screen.value = Screen.Menu
    }

    fun back() {
        if (_screen.value is Screen.Splash) return
        if (backStack.isEmpty()) {
            _screen.value = Screen.Menu
            return
        }
        val previous = backStack.removeAt(backStack.size - 1)
        _screen.value = previous
    }
}
