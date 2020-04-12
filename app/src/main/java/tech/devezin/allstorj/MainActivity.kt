package tech.devezin.allstorj

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import kotlinx.android.synthetic.main.activity_main.*
import tech.devezin.allstorj.utils.setupWithNavController

class MainActivity : AppCompatActivity() {

    private var currentNavController: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            setupBottomNavigation()
        }
    }

    private fun setupBottomNavigation() {
        val navGraphIds = listOf(R.navigation.buckets, R.navigation.settings)
        val navController = mainBottomNavigation.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.mainNavHostFragment,
            intent = intent
        )
        currentNavController = navController
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    override fun onResume() {
        super.onResume()
        setFragmentContainerBottomMargin()
    }

    private fun setFragmentContainerBottomMargin() {
        mainBottomNavigation.post {
            val height = mainBottomNavigation.height
            val params: CoordinatorLayout.LayoutParams =
                mainNavHostFragment.layoutParams as CoordinatorLayout.LayoutParams
            params.setMargins(0, 0, 0, height)
            mainNavHostFragment.layoutParams = params
        }
    }
}
