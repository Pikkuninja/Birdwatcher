package fi.jara.birdwatcher.screens.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fi.jara.birdwatcher.R
import kotlinx.android.synthetic.main.about_fragment.*
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import android.content.Intent
import android.net.Uri
import fi.jara.birdwatcher.BuildConfig


class AboutFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.about_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        about_open_source_code.setOnClickListener {
            val openBrowserIntent = Intent(Intent.ACTION_VIEW)
            openBrowserIntent.data = Uri.parse(BuildConfig.SOURCE_CODE_REPOSITORY_URL)
            startActivity(openBrowserIntent)
        }

        about_open_oss_licenses.setOnClickListener {
            startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java))
        }
    }
}