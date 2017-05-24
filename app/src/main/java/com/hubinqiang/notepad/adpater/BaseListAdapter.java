package com.hubinqiang.notepad.adpater;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressLint("UseSparseArrays")
public abstract class BaseListAdapter<E> extends BaseAdapter {

	public List<E> list;

	public Context mContext;

	public LayoutInflater mInflater;

	public List<E> getList() {
		return list;
	}

	public void setList(List<E> list) {
		this.list = list;
		notifyDataSetChanged();
	}



	public BaseListAdapter(Context context, List<E> list) {
		super();
		this.mContext = context;
		this.list = list;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public E getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = bindView(position, convertView, parent);
		addInternalClickListener(convertView, position, list.get(position));
		return convertView;
	}

	public abstract View bindView(int position, View convertView,
			ViewGroup parent);

	public Map<Integer, onInternalClickListener<E>> canClickItem;

	private void addInternalClickListener(final View itemV, final Integer position, final E valuesMap) {
		if (canClickItem != null) {
			for (Integer key : canClickItem.keySet()) {
				View inView = itemV.findViewById(key);
				final onInternalClickListener<E> listener = canClickItem.get(key);
				if (inView != null && listener != null) {
					inView.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							listener.OnClickListener(itemV, v, position,
                                    valuesMap);
						}
					});
                    inView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            listener.OnLongClickListener(itemV, v, position,
                                    valuesMap);
                            return true;
                        }
                    });
				}
			}
		}
	}



	public interface onInternalClickListener<T> {
		void OnClickListener(View parentV, View v, Integer position,
                             T values);
		void OnLongClickListener(View parentV, View v, Integer position,
                                 T values);
	}



}
