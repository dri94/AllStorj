package tech.devezin.allstorj

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import kotlinx.android.synthetic.main.activity_main.*
import tech.devezin.allstorj.onboarding.login.LoginFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.mainContainer, LoginFragment.newInstance()).commit()

        }
    }

    override fun onResume() {
        super.onResume()
        setFragmentContainerBottomMargin()
    }

    private fun setFragmentContainerBottomMargin() {
        mainBottomNavigation.post {
            val height = mainBottomNavigation.height
            val params: CoordinatorLayout.LayoutParams =
                mainContainer.layoutParams as CoordinatorLayout.LayoutParams
            params.setMargins(0, 0, 0, height)
            mainContainer.layoutParams = params
        }
    }
}
