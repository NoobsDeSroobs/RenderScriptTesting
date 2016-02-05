#pragma version(1)
#pragma rs java_package_name(com.imerso.renderscripttest)

uchar4 __attribute__((kernel)) doSomething(uint32_t x, uint32_t y){
    uchar4 ret = {(uchar)x, (uchar) y, (uchar) (x-y), (uchar)255};
    return ret;
}