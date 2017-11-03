package hahago.com.hahagotest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {


    String url = "https://hahago-api-tesla.appspot.com/fortest/api/";
    private CommonTask retrieveTask;
    private Double userlat,userlon;
    private TextView textview;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void findView(){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.board_rv);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(
                        1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(new BoardAdapter(this, getHahaVOList()));


        //*****如果有取得資料會使用的方法*****
        //recyclerView.setAdapter(new BoardAdapter(this, getData()));

    }

    private List<HahaVO> getHahaVOList() {
        List<HahaVO> hahaVOList = new ArrayList<HahaVO>();
        hahaVOList.add(getHahaVO());
        hahaVOList.add(getHahaVO());
        hahaVOList.add(getHahaVO());

        return hahaVOList;
    }

    private HahaVO getHahaVO(){
        HahaVO hahaVO = new HahaVO();
        hahaVO.setBody("測試:1-24 \n測試:1-24 \n測試:1-24");
        hahaVO.setUserName("Max Lin");
        hahaVO.setUid("mmax");
        hahaVO.setTimeDiff("21分鐘前");
        hahaVO.setDistance("0m");
        hahaVO.setPhoto("https://firebasestorage.googleapis.com/v0/b/hahago-tesla.appspot.com/o/" +
                "root_userPost%2FVmBk67bmySOgyGqYJ8gepGyk86l1%2F-Kxgu3EZaZ71d42QsmRU?alt=media&" +
                "token=23650104-1a56-4cea-ad25-f14a3c2feade");
        hahaVO.setUserPhoto("https://firebasestorage.googleapis.com/v0/b/hahago-tesla.appspot.com/o/" +
                "root_userprofiles%2FVmBk67bmySOgyGqYJ8gepGyk86l1%2FVmBk67bmySOgyGqYJ8gepGyk86l1?" +
                "alt=media&token6fa9e4fa-d6c0-4cd2-a320-9f2f243e582e");
        hahaVO.setNumGood(0);
        hahaVO.setNumBad(0);

        return hahaVO;
    }

    private List<HahaVO> getData() {
        String hahaData = "";
        userlat = 24.7844431;
        userlon = 121.0172038;
        retrieveTask = (CommonTask) new CommonTask().execute(url,  "userlat", userlat.toString(), "userlng", userlon.toString());

        String s = "";

        try {
            hahaData = retrieveTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        Type listType = new TypeToken<List<HahaVO>>(){}.getType();
        return gson.fromJson(hahaData, listType);
    }

    private class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.BoardViewHolder> {
        private Context context;
        private List<HahaVO> hahaVOList;

        public BoardAdapter(MainActivity mainActivity, List<HahaVO> hahaVOList) {
            this.context = mainActivity;
            this.hahaVOList = hahaVOList;
        }

        class BoardViewHolder extends RecyclerView.ViewHolder{
            TextView nameTv, postDetailMdTv, postDetailSmTv, bodyTv, numGoodTv, numBadTv;
            ImageView photoIv, userPhotoIv;

            public BoardViewHolder(View itemView) {
                super(itemView);

                nameTv = itemView.findViewById(R.id.name_tv);
                postDetailMdTv = itemView.findViewById(R.id.post_detail_md_tv);
                postDetailSmTv = itemView.findViewById(R.id.post_detail_sm_tv);
                bodyTv = itemView.findViewById(R.id.body_tv);
                numGoodTv = itemView.findViewById(R.id.num_good_tv);
                numBadTv = itemView.findViewById(R.id.num_bad_tv);
                photoIv = itemView.findViewById(R.id.photo_iv);
                userPhotoIv = itemView.findViewById(R.id.user_photo_iv);

            }
        }

        @Override
        public BoardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.board_card, parent, false);
            return new BoardViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(BoardViewHolder holder, int position) {
            HahaVO hahaVO = hahaVOList.get(position);

            holder.nameTv.setText(hahaVO.getUserName());
            holder.bodyTv.setText(hahaVO.getBody());
            holder.postDetailMdTv.setText(hahaVO.getUid());
            holder.postDetailSmTv.setText(" ‧ " + hahaVO.getTimeDiff() + " ‧ " + hahaVO.getDistance());
            holder.numGoodTv.setText(hahaVO.getNumGood().toString());
            holder.numBadTv.setText(hahaVO.getNumBad().toString());

            new DownloadImageTask((ImageView) holder.photoIv)
                    .execute(hahaVO.getPhoto());
            new DownloadImageTask((ImageView) holder.userPhotoIv)
                    .execute(hahaVO.getUserPhoto());
        }

        @Override
        public int getItemCount() {
            return hahaVOList.size();
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
                in.close();
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
