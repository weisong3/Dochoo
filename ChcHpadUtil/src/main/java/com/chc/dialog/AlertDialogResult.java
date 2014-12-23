package com.chc.dialog;

public interface AlertDialogResult extends DialogResult {
  public void onConfirmed(int id);
  public void onCancelled(int id);
}
