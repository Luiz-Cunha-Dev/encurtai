"use client";

import { useEffect, useMemo, useState, useCallback } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { toast } from "sonner";
import { Copy, Trash2, ChevronLeft, ChevronRight, BarChart3, Loader2 } from "lucide-react";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  Pagination,
  PaginationContent,
  PaginationItem,
} from "@/components/ui/pagination";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/ui/alert-dialog";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Skeleton } from "@/components/ui/skeleton";

import type { ShortLink, Pagination as PaginationType } from "@/types";
import { createShortLink, deleteShortLink, getLinks, getLinkMetrics } from "@/lib/api";

const formSchema = z.object({
  mainUrl: z.string().url({ message: "Por favor, insira uma URL válida." }),
});

function MetricsButton({ token }: { token: string }) {
  const [isOpen, setIsOpen] = useState(false);
  const [metrics, setMetrics] = useState<{ count: number } | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const fetchMetrics = async () => {
    setIsLoading(true);
    setMetrics(null);
    try {
      const data = await getLinkMetrics(token);
      setMetrics(data);
    } catch (error) {
      toast.error("Falha ao buscar métricas.", {
        description:
          error instanceof Error
            ? error.message
            : "Tente novamente mais tarde.",
      });
    } finally {
      setIsLoading(false);
    }
  };

  const handleOpenChange = (open: boolean) => {
    setIsOpen(open);
    if (open) {
      fetchMetrics();
    }
  };

  return (
    <Dialog open={isOpen} onOpenChange={handleOpenChange}>
      <DialogTrigger asChild>
        <Button variant="ghost" size="icon" title="Ver métricas">
          <BarChart3 className="h-4 w-4 text-blue-500" />
        </Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>Métricas do Link</DialogTitle>
          <DialogDescription>
            Estatísticas de acesso para este link encurtado.
          </DialogDescription>
        </DialogHeader>
        <div className="flex items-center justify-center py-8">
          {isLoading ? (
            <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
          ) : metrics !== null ? (
            <div className="text-center">
              <div className="text-5xl font-bold text-primary">{metrics.count}</div>
              <p className="text-muted-foreground mt-2">
                {metrics.count === 1 ? "acesso" : "acessos"}
              </p>
            </div>
          ) : (
            <p className="text-muted-foreground">Não foi possível carregar as métricas.</p>
          )}
        </div>
      </DialogContent>
    </Dialog>
  );
}

export function UrlShortener() {
  const [links, setLinks] = useState<ShortLink[]>([]);
  const [pagination, setPagination] = useState<PaginationType>({
    page: 0,
    limit: 5,
    total: 0,
  });
  const [isLoading, setIsLoading] = useState(true);

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      mainUrl: "",
    },
  });

  const fetchLinks = useCallback(async () => {
    setIsLoading(true);
    try {
      const response = await getLinks(pagination.page, pagination.limit);
      setLinks(response.data);
      setPagination(response.pagination);
    } catch (error) {
      toast.error("Falha ao buscar os links.", {
        description:
          error instanceof Error
            ? error.message
            : "Tente novamente mais tarde.",
      });
    } finally {
      setIsLoading(false);
    }
  }, [pagination.page, pagination.limit]);

  useEffect(() => {
    fetchLinks();
  }, [fetchLinks]);

  const onSubmit = async (values: z.infer<typeof formSchema>) => {
    try {
      const newLink = await createShortLink(values.mainUrl);
      toast.success("URL encurtada com sucesso!", {
        description: `Sua URL curta é: ${newLink.shortenedUrl}`,
      });
      form.reset();
      if (pagination.page !== 0) {
        handlePageChange(0);
      } else {
        fetchLinks();
      }
    } catch (error) {
      toast.error("Falha ao encurtar a URL.", {
        description:
          error instanceof Error
            ? error.message
            : "Verifique a URL e tente novamente.",
      });
    }
  };

  const handleDelete = async (shortUrl: string) => {
    const token = shortUrl.split("/").pop();
    if (!token) {
      toast.error("URL inválida para exclusão.");
      return;
    }

    try {
      await deleteShortLink(token);
      toast.success("Link excluído com sucesso.");
      if (links.length === 1 && pagination.page > 0) {
        handlePageChange(pagination.page - 1);
      } else {
        fetchLinks();
      }
    } catch (error) {
      toast.error("Falha ao excluir o link.", {
        description:
          error instanceof Error
            ? error.message
            : "Tente novamente mais tarde.",
      });
    }
  };

  const handleCopy = (url: string) => {
    navigator.clipboard.writeText(url);
    toast.success("Copiado para a área de transferência!");
  };

  const handlePageChange = (newPage: number) => {
    const totalPages = Math.ceil(pagination.total / pagination.limit);
    if (newPage >= 0 && newPage < totalPages) {
        setPagination(prev => ({ ...prev, page: newPage }));
    } else if (newPage < 0 && totalPages > 0) {
        setPagination(prev => ({ ...prev, page: 0 }));
    }
  };

  const totalPages = Math.ceil(pagination.total / pagination.limit);

  const paginationRange = useMemo(() => {
    const delta = 1;
    const range = [];
    const left = pagination.page - delta;
    const right = pagination.page + delta;

    for (let i = 0; i < totalPages; i++) {
      if (i === 0 || i === totalPages - 1 || (i >= left && i <= right)) {
        range.push(i);
      }
    }

    const withDots: (number | string)[] = [];
    let last: number | undefined;
    for (const page of range) {
      if (last !== undefined) {
        if (page - last === 2) {
          withDots.push(last + 1);
        } else if (page - last !== 1) {
          withDots.push("...");
        }
      }
      withDots.push(page);
      last = page;
    }
    return withDots;
  }, [pagination.page, totalPages]);

  return (
    <div className="space-y-8">
      <Card>
        <CardHeader>
          <CardTitle>Criar um novo Link Curto</CardTitle>
        </CardHeader>
        <CardContent>
          <Form {...form}>
            <form
              onSubmit={form.handleSubmit(onSubmit)}
              className="flex flex-col sm:flex-row gap-4 items-baseline"
            >
              <FormField
                control={form.control}
                name="mainUrl"
                render={({ field }) => (
                  <FormItem className="flex-grow w-full">
                    <FormLabel className="sr-only">URL</FormLabel>
                    <FormControl>
                      <Input placeholder="https://example.com" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <Button
                type="submit"
                disabled={form.formState.isSubmitting}
                className="w-full sm:w-auto"
              >
                {form.formState.isSubmitting ? "Encurtando..." : "Encurtar"}
              </Button>
            </form>
          </Form>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Seus Links</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="border rounded-md">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>URL Original</TableHead>
                  <TableHead>URL Curta</TableHead>
                  <TableHead className="text-right">Ações</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {isLoading ? (
                  Array.from({ length: 5 }).map((_, index) => (
                    <TableRow key={index}>
                      <TableCell>
                        <Skeleton className="h-5 w-48" />
                      </TableCell>
                      <TableCell>
                        <Skeleton className="h-5 w-36" />
                      </TableCell>
                      <TableCell className="text-right">
                        <div className="flex justify-end gap-2">
                          <Skeleton className="h-8 w-8" />
                          <Skeleton className="h-8 w-8" />
                          <Skeleton className="h-8 w-8" />
                        </div>
                      </TableCell>
                    </TableRow>
                  ))
                ) : links.length > 0 ? (
                  links.map((link) => (
                    <TableRow key={link.shortenedUrl}>
                      <TableCell className="max-w-xs truncate">
                        <a
                          href={link.mainUrl}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="hover:underline"
                        >
                          {link.mainUrl}
                        </a>
                      </TableCell>
                      <TableCell>
                        <a
                          href={link.shortenedUrl}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="hover:underline text-primary"
                        >
                          {link.shortenedUrl}
                        </a>
                      </TableCell>
                      <TableCell className="text-right">
                        <Button
                          variant="ghost"
                          size="icon"
                          onClick={() => handleCopy(link.shortenedUrl)}
                          title="Copiar link"
                        >
                          <Copy className="h-4 w-4" />
                        </Button>
                        <MetricsButton token={link.shortenedUrl.split("/").pop() || ""} />
                        <AlertDialog>
                          <AlertDialogTrigger asChild>
                            <Button variant="ghost" size="icon" title="Excluir link">
                              <Trash2 className="h-4 w-4 text-red-500" />
                            </Button>
                          </AlertDialogTrigger>
                          <AlertDialogContent>
                            <AlertDialogHeader>
                              <AlertDialogTitle>
                                Você tem certeza absoluta?
                              </AlertDialogTitle>
                              <AlertDialogDescription>
                                Esta ação não pode ser desfeita. Isso excluirá
                                permanentemente este link curto.
                              </AlertDialogDescription>
                            </AlertDialogHeader>
                            <AlertDialogFooter>
                              <AlertDialogCancel>Cancelar</AlertDialogCancel>
                              <AlertDialogAction
                                onClick={() => handleDelete(link.shortenedUrl)}
                              >
                                Excluir
                              </AlertDialogAction>
                            </AlertDialogFooter>
                          </AlertDialogContent>
                        </AlertDialog>
                      </TableCell>
                    </TableRow>
                  ))
                ) : (
                  <TableRow>
                    <TableCell colSpan={3} className="text-center py-12">
                      Nenhum link encontrado. Crie um acima!
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </div>
          {totalPages > 1 && (
            <div className="mt-4">
              <Pagination>
                <PaginationContent>
                  <PaginationItem>
                    <Button
                      variant="outline"
                      size="icon"
                      onClick={() => handlePageChange(pagination.page - 1)}
                      disabled={pagination.page <= 0}
                    >
                      <ChevronLeft className="h-4 w-4" />
                      <span className="sr-only">Anterior</span>
                    </Button>
                  </PaginationItem>

                  <div className="hidden sm:flex items-center gap-1">
                    {paginationRange.map((page, index) => (
                      <PaginationItem key={typeof page === "string" ? `dots-${index}` : `page-${page}`}>
                        {typeof page === "string" ? (
                          <span className="px-2">...</span>
                        ) : (
                          <Button
                            variant={
                              page === pagination.page ? "default" : "outline"
                            }
                            size="icon"
                            onClick={() => handlePageChange(page)}
                          >
                            {page + 1}
                          </Button>
                        )}
                      </PaginationItem>
                    ))}
                  </div>

                  <PaginationItem className="sm:hidden">
                    <span className="px-4 py-2 text-sm font-medium">
                      Página {pagination.page + 1} de {totalPages}
                    </span>
                  </PaginationItem>

                  <PaginationItem>
                    <Button
                      variant="outline"
                      size="icon"
                      onClick={() => handlePageChange(pagination.page + 1)}
                      disabled={pagination.page >= totalPages - 1}
                    >
                      <ChevronRight className="h-4 w-4" />
                      <span className="sr-only">Próximo</span>
                    </Button>
                  </PaginationItem>
                </PaginationContent>
              </Pagination>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}