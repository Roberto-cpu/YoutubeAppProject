package roberto.garzone.youtubereviews.activities

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import roberto.garzone.youtubereviews.R
import roberto.garzone.youtubereviews.models.Song

class FeedbackGraphActivity : AppCompatActivity() {

    private lateinit var mLayout : ConstraintLayout
    private lateinit var mToolbar : Toolbar
    private lateinit var mBackBtn : Button
    private lateinit var mPieChart : PieChart
    private lateinit var m1star : TextView
    private lateinit var m2star : TextView
    private lateinit var m3star : TextView
    private lateinit var m4star : TextView
    private lateinit var m5star : TextView

    private var night = ""
    private lateinit var song : Song
    private lateinit var songs : ArrayList<Song>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.feedback_graph_layout)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        mLayout = findViewById(R.id.graph_view_main_layout)
        mToolbar = findViewById(R.id.graph_view_toolbar)
        mBackBtn = findViewById(R.id.graph_view_back_btn)
        mPieChart = findViewById(R.id.pie_chart_songs_feed)
        m1star = findViewById(R.id.graph_view_one_star_text)
        m2star = findViewById(R.id.graph_view_two_stars_text)
        m3star = findViewById(R.id.graph_view_three_stars_text)
        m4star = findViewById(R.id.graph_view_four_stars_text)
        m5star = findViewById(R.id.graph_view_five_stars_text)

        setSupportActionBar(mToolbar)
        supportActionBar!!.title = ""

        val getIntent = intent

        song = getIntent.getSerializableExtra("song") as Song
        night = getIntent.getStringExtra("night mode").toString()
        songs = getIntent.getSerializableExtra("songs") as ArrayList<Song>

        mBackBtn.setOnClickListener {
            val backIntent = Intent(this@FeedbackGraphActivity, ReviewsListActivity::class.java)
            backIntent.putExtra("name", song.getName())
            backIntent.putExtra("night mode", night)
            backIntent.putExtra("songs", songs)
            startActivity(backIntent)
            finish()
        }

        initPieChart()
        setUpPieChart()
    }

    private fun setUpPieChart() {
        var entries : ArrayList<PieEntry> = ArrayList()
        var typeMap : HashMap<String, Int> = HashMap()
        var label = "Type"

        typeMap[resources.getString(R.string.graph_view_one_star)] = 1
        typeMap[resources.getString(R.string.graph_view_two_stars)] = 2
        typeMap[resources.getString(R.string.graph_view_three_stars)] = 3
        typeMap[resources.getString(R.string.graph_view_four_stars)] = 4
        typeMap[resources.getString(R.string.graph_view_five_stars)] = 5

        var colors : ArrayList<Int> = ArrayList()
        colors.add(ResourcesCompat.getColor(resources, R.color.colorRed, null))
        colors.add(ResourcesCompat.getColor(resources, R.color.colorBlue, null))
        colors.add(ResourcesCompat.getColor(resources, R.color.colorGreen, null))
        colors.add(ResourcesCompat.getColor(resources, R.color.colorViolet, null))
        colors.add(ResourcesCompat.getColor(resources, R.color.colorYellow, null))

        for (type : String in typeMap.keys)
            entries.add(PieEntry(typeMap[type]!!.toFloat(), type))

        var pieDataSet = PieDataSet(entries, label)
        pieDataSet.valueTextSize = 16f
        pieDataSet.colors = colors

        var pieData = PieData(pieDataSet)
        pieData.setDrawValues(true)

        mPieChart.data = pieData
        mPieChart.invalidate()
    }

    private fun initPieChart() {
        mPieChart.setUsePercentValues(false)
        mPieChart.description.isEnabled = false
        mPieChart.isRotationEnabled = false
        mPieChart.dragDecelerationFrictionCoef = 0.9f
        mPieChart.rotationAngle = 0f
        mPieChart.isHighlightPerTapEnabled = true
        mPieChart.animateY(1400, Easing.EaseInOutQuad)
        mPieChart.setHoleColor(ResourcesCompat.getColor(resources, R.color.colorBlack, null))
    }
}