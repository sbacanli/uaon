nvcc -ptx distance.cu -o distance.ptx
:nvcc -cubin -m64 -arch sm_50 distance.cu -o distance.cubin
nvcc -cubin -m64 -gencode arch=compute_50,code=sm_50 distance.cu -o distance.cubin
