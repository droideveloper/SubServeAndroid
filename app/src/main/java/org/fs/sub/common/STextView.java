/*
 * SubServe Android Copyright (C) 2016 Fatih.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fs.sub.common;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class STextView extends TextView implements ITextView {

  /**
   * TODO find a way to implement stroke over textView
   * TODO make sure that \n is not effected with onDraw(canvas) implementations
   */

  public STextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  public STextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public STextView(Context context) {
    super(context);
    init();
  }

  private void init() {
    setDrawingCacheEnabled(false);
    strokeThroughText();
  }

  @Override public float shadowRadius() {
    return 0.25f * getTextSize();
  }

  @Override public void strokeThroughText() {
    setShadowLayer(shadowRadius(), 0f, 0f, invertColor());
  }

  @Override public int invertColor() {
    int color = getCurrentTextColor();
    return (color & 0xFF000000) | (~color & 0x00FFFFFF);
  }
}
