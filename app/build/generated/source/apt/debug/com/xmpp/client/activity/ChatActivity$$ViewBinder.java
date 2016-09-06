// Generated code from Butter Knife. Do not modify!
package com.xmpp.client.activity;

import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Finder;
import butterknife.internal.ViewBinder;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class ChatActivity$$ViewBinder<T extends ChatActivity> implements ViewBinder<T> {
  @Override
  public Unbinder bind(final Finder finder, final T target, Object source) {
    InnerUnbinder unbinder = createUnbinder(target);
    View view;
    view = finder.findRequiredView(source, 2131230725, "field 'mMsgText'");
    target.mMsgText = finder.castView(view, 2131230725, "field 'mMsgText'");
    view = finder.findRequiredView(source, 2131230723, "field 'mPtrogressBar'");
    target.mPtrogressBar = finder.castView(view, 2131230723, "field 'mPtrogressBar'");
    view = finder.findRequiredView(source, 2131230724, "field 'mListView'");
    target.mListView = finder.castView(view, 2131230724, "field 'mListView'");
    view = finder.findRequiredView(source, 2131230726, "field 'btsend' and method 'onclick'");
    target.btsend = finder.castView(view, 2131230726, "field 'btsend'");
    unbinder.view2131230726 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onclick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131230727, "field 'btattach' and method 'onclick'");
    target.btattach = finder.castView(view, 2131230727, "field 'btattach'");
    unbinder.view2131230727 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onclick(p0);
      }
    });
    return unbinder;
  }

  protected InnerUnbinder<T> createUnbinder(T target) {
    return new InnerUnbinder(target);
  }

  protected static class InnerUnbinder<T extends ChatActivity> implements Unbinder {
    private T target;

    View view2131230726;

    View view2131230727;

    protected InnerUnbinder(T target) {
      this.target = target;
    }

    @Override
    public final void unbind() {
      if (target == null) throw new IllegalStateException("Bindings already cleared.");
      unbind(target);
      target = null;
    }

    protected void unbind(T target) {
      target.mMsgText = null;
      target.mPtrogressBar = null;
      target.mListView = null;
      view2131230726.setOnClickListener(null);
      target.btsend = null;
      view2131230727.setOnClickListener(null);
      target.btattach = null;
    }
  }
}
