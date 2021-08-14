package com.teamwizardry.librarianlib.core.bridge;

public interface IMatrix3f {
    /** Get row 0, column 0 */
    float getM00();
    /** Set row 0, column 0 */
    void setM00(float v);
    /** Get row 0, column 1 */
    float getM01();
    /** Set row 0, column 1 */
    void setM01(float v);
    /** Get row 0, column 2 */
    float getM02();
    /** Set row 0, column 2 */
    void setM02(float v);
    /** Get row 1, column 0 */
    float getM10();
    /** Set row 1, column 0 */
    void setM10(float v);
    /** Get row 1, column 1 */
    float getM11();
    /** Set row 1, column 1 */
    void setM11(float v);
    /** Get row 1, column 2 */
    float getM12();
    /** Set row 1, column 2 */
    void setM12(float v);
    /** Get row 2, column 0 */
    float getM20();
    /** Set row 2, column 0 */
    void setM20(float v);
    /** Get row 2, column 1 */
    float getM21();
    /** Set row 2, column 1 */
    void setM21(float v);
    /** Get row 2, column 2 */
    float getM22();
    /** Set row 2, column 2 */
    void setM22(float v);

    float transformX(float x, float y, float z);
    float transformY(float x, float y, float z);
    float transformZ(float x, float y, float z);

    float transformX(float x, float y);
    float transformY(float x, float y);
}
