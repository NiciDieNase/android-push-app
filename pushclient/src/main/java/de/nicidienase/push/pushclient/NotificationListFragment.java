package de.nicidienase.push.pushclient;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ListView;

import com.activeandroid.content.ContentProvider;
import com.activeandroid.query.Select;

import de.nicidienase.push.pushclient.Model.Notification;

/**
* Created by felix on 07/10/14.
*/
public class NotificationListFragment extends ListFragment {

	private Callbacks mCallback;
	private int mActivePosition;

	public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(Notification notification);
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		NotificationCursorAdapter cursorAdapter = new NotificationCursorAdapter(getActivity());

		setListAdapter(cursorAdapter);
		updateCursor();
	}

	public void updateCursor() {
		getActivity().getSupportLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
			@Override
			public Loader<Cursor> onCreateLoader(int arg0, Bundle cursor) {
				return new CursorLoader(getActivity(),
						ContentProvider.createUri(Notification.class, null),
						null, null, null, "received DESC"
				);
			}

			@Override
			public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
				((SimpleCursorAdapter)getListAdapter()).swapCursor(cursor);
			}

			@Override
			public void onLoaderReset(Loader<Cursor> arg0) {
				((SimpleCursorAdapter)getListAdapter()).swapCursor(null);
			}
		});
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if(savedInstanceState != null
				&& savedInstanceState.containsKey("ACTIVE_ITEM")){
			setActivatedPosition(savedInstanceState.getInt("ACTIVE_ITEM"));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(mActivePosition != ListView.INVALID_POSITION){
			outState.putInt("ACTIVE_ITEM",mActivePosition);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if(!( activity instanceof Callbacks)){
			throw new IllegalStateException("acitivity does not implement callbacks");
		}
		this.mCallback = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		this.mCallback = null;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		mCallback.onItemSelected((Notification) new Select().from(Notification.class).where("_id = ?", id).executeSingle());
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(activateOnItemClick
				? ListView.CHOICE_MODE_SINGLE
				: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivePosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivePosition = position;
	}
}
