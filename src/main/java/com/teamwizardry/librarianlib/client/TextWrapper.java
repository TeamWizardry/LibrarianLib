package com.teamwizardry.librarianlib.client;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;

public class TextWrapper {
	public static void wrap(FontRenderer renderer, List<String> list, String str, int initialLinePos, int width) {
		
		int i = sizeStringToWidth(renderer, str, width-initialLinePos);
		if (str.length() <= i) {
			list.add(str);
		} else {
			String s = str.substring(0, i);
            list.add(s);
            char c0 = str.charAt(i);
            boolean flag = c0 == 32 || c0 == 10;
            String s1 = FontRenderer.getFormatFromString(s) + str.substring(i + (flag ? 1 : 0));
            
            wrap(renderer, list, s1, 0, width);
		}
	}
	
	private static int sizeStringToWidth(FontRenderer renderer, String str, int wrapWidth)
    {
        int i = str.length();
        int j = 0;
        int k = 0;
        int l = -1;

        for (boolean flag = false; k < i; ++k)
        {
            char c0 = str.charAt(k);

            switch (c0)
            {
                case '\n':
                    --k;
                    break;
                case ' ':
                    l = k;
                default:
                    j += renderer.getCharWidth(c0);

                    if (flag)
                    {
                        ++j;
                    }

                    break;
                case '\u00a7':

                    if (k < i - 1)
                    {
                        ++k;
                        char c1 = str.charAt(k);

                        if (c1 != 108 && c1 != 76)
                        {
                            if (c1 == 114 || c1 == 82 || isFormatColor(c1))
                            {
                                flag = false;
                            }
                        }
                        else
                        {
                            flag = true;
                        }
                    }
            }

            if (c0 == 10)
            {
                ++k;
                l = k;
                break;
            }

            if (j > wrapWidth)
            {
                break;
            }
        }

        return k != i && l != -1 && l < k ? l : k;
    }
	
	public static boolean isFormatColor(char colorChar)
    {
        return colorChar >= 48 && colorChar <= 57 || colorChar >= 97 && colorChar <= 102 || colorChar >= 65 && colorChar <= 70;
    }
}
