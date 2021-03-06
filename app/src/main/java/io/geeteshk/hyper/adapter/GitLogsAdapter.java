package io.geeteshk.hyper.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.jgit.revwalk.RevCommit;

import java.util.List;

import io.geeteshk.hyper.R;

public class GitLogsAdapter extends RecyclerView.Adapter<GitLogsAdapter.ViewHolder> {

    private Context mContext;
    private List<RevCommit> mLogs;

    public GitLogsAdapter(Context context, List<RevCommit> logs) {
        mContext = context;
        mLogs = logs;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_git_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final RevCommit commit = mLogs.get(position);
        final SpannableString string = new SpannableString(commit.getFullMessage());
        int index = commit.getFullMessage().indexOf('\n') + 1;
        if (index == 0) index = commit.getFullMessage().length();
        final boolean[] fullShown = {false};
        string.setSpan(new StyleSpan(Typeface.BOLD), 0, index, 0);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!fullShown[0]) {
                    holder.mCommitName.setTypeface(Typeface.DEFAULT);
                    holder.mCommitName.setText(string);
                    fullShown[0] = true;
                } else {
                    holder.mCommitName.setTypeface(Typeface.DEFAULT_BOLD);
                    holder.mCommitName.setText(commit.getShortMessage());
                    fullShown[0] = false;
                }
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("hash", commit.getId().getName());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(mContext, "Commit hash copied.", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        holder.mCommitName.setText(commit.getShortMessage());
        holder.mCommitName.setTypeface(Typeface.DEFAULT_BOLD);
        holder.mCommitAuthor.setText(commit.getAuthorIdent().getName() + " <" + commit.getAuthorIdent().getEmailAddress() + ">");
        holder.mCommitDate.setText(commit.getAuthorIdent().getWhen().toString());
        holder.mCommitHash.setText(commit.getId().getName());
    }

    @Override
    public int getItemCount() {
        if (mLogs != null) {
            return mLogs.size();
        } else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mCommitName, mCommitDate, mCommitAuthor, mCommitHash;
        public View mView;

        public ViewHolder(View v) {
            super(v);
            mView = v;
            mCommitName = (TextView) v.findViewById(R.id.commit_name);
            mCommitDate = (TextView) v.findViewById(R.id.commit_date);
            mCommitAuthor = (TextView) v.findViewById(R.id.commit_author);
            mCommitHash = (TextView) v.findViewById(R.id.commit_hash);
        }
    }
}
