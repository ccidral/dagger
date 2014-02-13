package dagger.servlet3;

public class TestFeatureA implements ServletFeature {

    private static boolean isEnabled;

    public static boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void enable() {
        TestFeatureA.isEnabled = true;
    }

}
