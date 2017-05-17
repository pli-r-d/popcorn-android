/*
 * This file is part of Butter.
 *
 * Butter is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Butter is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Butter. If not, see <http://www.gnu.org/licenses/>.
 */

package butter.droid.ui.trailer;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import butter.droid.MobileButterApplication;
import butter.droid.base.providers.media.models.Media;
import butter.droid.base.torrent.StreamInfo;
import butter.droid.base.ui.dialog.DialogFactory;
import butter.droid.base.ui.dialog.DialogFactory.Action;
import butter.droid.base.ui.dialog.DialogFactory.ActionCallback;
import butter.droid.ui.ButterBaseActivity;
import butter.droid.ui.trailer.fragment.TrailerPlayerFragment;
import javax.inject.Inject;

public class TrailerPlayerActivity extends ButterBaseActivity implements TrailerPlayerView {

    private final static String EXTRA_URI = "butter.droid.ui.trailer.TrailerPlayerActivity.uri";
    private final static String EXTRA_MEDIA = "butter.droid.ui.trailer.TrailerPlayerActivity.media";

    private final static String TAG_VIDEO_FRAGMENT = "butter.droid.ui.player.VideoPlayerActivity.videoFragment";

    @Inject TrailerPlayerPresenter presenter;

    private TrailerPlayerFragment playerFragment;

    TrailerPlayerComponent component;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        component = MobileButterApplication.getAppContext()
                .getComponent()
                .trailerComponentBuilder()
                .trailerModule(new TrailerPlayerModule(this))
                .build();
        component.inject(this);

        super.onCreate(savedInstanceState, 0);

        final Intent intent = getIntent();
        final Media media = intent.getParcelableExtra(EXTRA_MEDIA);
        final String youtubeUrl = intent.getStringExtra(EXTRA_URI);

//        this.playerFragment = (PlayerFragment) getSupportFragmentManager().findFragmentById(R.id.video_fragment);

        if (savedInstanceState == null) {
            playerFragment = TrailerPlayerFragment.newInstance(media, youtubeUrl);
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, playerFragment, TAG_VIDEO_FRAGMENT)
                    .commit();
//            presenter.onCreate(streamInfo, resumePosition, intent.getAction(), intent);
        } else {
            playerFragment = (TrailerPlayerFragment) getSupportFragmentManager().findFragmentByTag(TAG_VIDEO_FRAGMENT);
        }
//        presenter.onCreate(media, youtubeUrl);
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Long getResumePosition() {
        return 0L;
    }

    public StreamInfo getInfo() {
        return presenter.getStreamInfo();
    }

    @Override
    public void onDisableVideoPlayerSubsButton() {
//        playerFragment.enableSubsButton(false);
    }

    @Override
    public void onNotifyMediaReady() {
//        playerFragment.onMediaReady();
    }

    @Override
    public void onDisplayErrorVideoDialog() {
        DialogFactory.createErrorFetchingYoutubeVideoDialog(this, new ActionCallback() {
            @Override
            public void onButtonClick(final Dialog which, final @Action int action) {
                finish();
            }
        }).show();
    }

    public TrailerPlayerComponent getComponent() {
        return component;
    }

    public static Intent getIntent(final Context context, final Media media, final String url) {
        final Intent intent = new Intent(context, TrailerPlayerActivity.class);
        intent.putExtra(TrailerPlayerActivity.EXTRA_MEDIA, media);
        intent.putExtra(TrailerPlayerActivity.EXTRA_URI, url);
        return intent;
    }
}
