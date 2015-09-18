package land.josh.android.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by josh on 8/30/15.
 */
public class CrimeListActivity extends SingleFragmentActivity implements
    CrimeListFragment.Callbacks {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    public void onCrimeSelected(Crime crime) {
    }

}
