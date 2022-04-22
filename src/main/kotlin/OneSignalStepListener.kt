interface OneSignalStepListener {

    fun onNextStep()
    fun onStepCancel()

    fun getAppDirectory(): String
    fun setAppDirectory(directory:  String)

    fun getAppBuildGradlePath(): String?
    fun setAppBuildGradlePath(path:  String)
}
