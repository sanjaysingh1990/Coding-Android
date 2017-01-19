package net.coding.program.setting;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;

import net.coding.program.EnterpriseApp;
import net.coding.program.R;
import net.coding.program.common.Global;
import net.coding.program.common.ui.BackActivity;
import net.coding.program.common.ui.shadow.RecyclerViewSpace;
import net.coding.program.model.TaskObject;
import net.coding.program.model.team.TeamMember;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_manage_member)
public class ManageMemberActivity extends BackActivity {

    private static final String TAG_PROJECT = "TAG_PROJECT";

    @ViewById
    UltimateRecyclerView listView;

    MemberAdapter adapter;

    ArrayList<TeamMember> listData = new ArrayList<>();

    @AfterViews
    void initManageMemberActivity() {
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.addItemDecoration(new RecyclerViewSpace(this));
        listView.setEmptyView(R.layout.fragment_enterprise_project_empty, R.layout.fragment_enterprise_project_empty);

        adapter = new MemberAdapter(listData);
        listView.setAdapter(adapter);
        listView.setDefaultOnRefreshListener(() -> onRefresh());
        listView.enableDefaultSwipeRefresh(true);

        onRefresh();
    }

    private void onRefresh() {
        String host = String.format("%s/team/%s/members", Global.HOST_API, EnterpriseApp.getEnterpriseGK());
        getNetwork(host, TAG_PROJECT);
    }

    protected class MemberAdapter extends easyRegularAdapter<TeamMember, ProjectHolder> {

        public MemberAdapter(List<TeamMember> list) {
            super(list);
        }

        @Override
        protected int getNormalLayoutResId() {
            return R.layout.enterprise_manage_member_list_item;
        }

        @Override
        protected ProjectHolder newViewHolder(View view) {
            return new ProjectHolder(view);
        }

        @Override
        protected void withBindHolder(ProjectHolder holder, TeamMember data, int position) {
            TeamMember item = getItem(position);
            holder.name.setText(item.user.name);
            holder.joinTime.setText(String.format("加入时间：%s", Global.mDateYMDHH.format(data.updatedat)));
            holder.rootLayout.setTag(item);

            TaskObject.Members.Type memberType = item.getType();
            int iconRes = memberType.getIcon();
            holder.name.setCompoundDrawablesWithIntrinsicBounds(0, 0, iconRes, 0);
            iconfromNetwork(holder.image, item.user.avatar);
        }

        @Override
        public ProjectHolder newHeaderHolder(View view) {
            return new ProjectHolder(view, true);
        }

        @Override
        public ProjectHolder newFooterHolder(View view) {
            return new ProjectHolder(view, true);
        }
    }

    public static class ProjectHolder extends UltimateRecyclerviewViewHolder {

        public TextView name;
        public TextView joinTime;
        public ImageView image;
        public View rootLayout;

        public ProjectHolder(View view, boolean isHeader) {
            super(view);
        }

        public ProjectHolder(View view) {
            super(view);
            rootLayout = view;
            name = (TextView) view.findViewById(R.id.name);
            joinTime = (TextView) view.findViewById(R.id.joinTime);
            image = (ImageView) view.findViewById(R.id.icon);
        }
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        if (tag.equals(TAG_PROJECT)) {
            if (code == 0) {
                listData.clear();
                JSONArray array = respanse.optJSONArray("data");
                for (int i = 0; i < array.length(); ++i) {
                    listData.add(new TeamMember(array.optJSONObject(i)));
                }

                adapter.notifyDataSetChanged();
            } else {
                showErrorMsg(code, respanse);
            }
        }
    }
}